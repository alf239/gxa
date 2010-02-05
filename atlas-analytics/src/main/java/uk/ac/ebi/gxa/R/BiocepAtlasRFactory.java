package uk.ac.ebi.gxa.R;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.kchine.r.RNumeric;
import org.kchine.r.server.RConsoleAction;
import org.kchine.r.server.RConsoleActionListener;
import org.kchine.r.server.RServices;
import org.kchine.rpf.RemoteLogListener;
import org.kchine.rpf.ServantProvider;
import org.kchine.rpf.ServantProviderFactory;
import org.kchine.rpf.db.ServantProxyPoolSingletonDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.NoSuchElementException;

/**
 * A concrete implementation of {@link uk.ac.ebi.gxa.R.AtlasRFactory} that generates RServices that run on a biocep
 * compute cloud.  This requires a biocep.properties file to be located on the classpath, as information about the
 * biocep environment will be read from this file prior to initialization of any services.
 *
 * @author Misha Kapushesky
 * @author Tony Burdett
 * @date 17-Nov-2009
 */
public class BiocepAtlasRFactory implements AtlasRFactory {
    private volatile boolean isInitialized = false;

    private GenericObjectPool workerPool;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Validates that all the system properties required by biocep are set.
     *
     * @return true if the validation succeed, flase if it failed for a reason OTHER than a missing property
     * @throws AtlasRServicesException if any required properties are missing.
     */
    public boolean validateEnvironment() throws AtlasRServicesException {
        if (System.getProperty("pools.dbmode.host") == null) {
            log.warn("pools.dbmode.host not set");
            return false;
        }
        if (System.getProperty("pools.dbmode.port") == null) {
            log.warn("pools.dbmode.port not set");
            return false;
        }
        if (System.getProperty("pools.dbmode.name") == null) {
            log.warn("pools.dbmode.name not set");
            return false;
        }
        if (System.getProperty("pools.dbmode.user") == null) {
            log.warn("biocep.dbmode.user not set");
            return false;
        }
        if (System.getProperty("pools.dbmode.password") == null) {
            log.warn("biocep.db.password not set");
            return false;
        }
        if (System.getProperty("naming.mode") == null) {
            log.warn("biocep.naming.mode not set");
            return false;
        }
        if (System.getProperty("pools.provider.factory") == null) {
            log.warn("biocep.provider.factory not set");
            return false;
        }
        if (System.getProperty("pools.dbmode.type") == null) {
            log.warn("biocep.db.type not set");
            return false;
        }
        if (System.getProperty("pools.dbmode.driver") == null) {
            log.warn("pools.dbmode.driver not set");
            return false;
        }
        if (System.getProperty("pools.dbmode.defaultpoolname") == null) {
            log.warn("pools.dbmode.defaultpoolname not set");
            return false;
        }
        if (System.getProperty("pools.dbmode.killused") == null) {
            log.warn("pools.dbmode.killused not set");
            return false;
        }

        // otherwise, checks passed so return true
        return true;
    }

    public RServices createRServices() throws AtlasRServicesException {
        // lazily initialize servant provider
        initialize();

        log.trace("Worker pool before borrow... " +
                "active = " + workerPool.getNumActive() + ", idle = " + workerPool.getNumIdle());
        try {
            // borrow a worker
            RServices rServices = (RServices) workerPool.borrowObject();
            log.debug("Borrowed " + rServices.getServantName() + " from the pool");
            return rServices;
        }
        catch (Exception e) {
            e.printStackTrace();
            log.debug("borrowObject() threw an exception: {}", e.getMessage());
            throw new AtlasRServicesException(
                    "Failed to borrow an RServices object from the pool of workers", e);
        }
        finally {
            log.trace("Worker pool after borrow... " +
                    "active = " + workerPool.getNumActive() + ", idle = " + workerPool.getNumIdle());
        }
    }

    public void recycleRServices(RServices rServices)
            throws UnsupportedOperationException, AtlasRServicesException {
        log.trace("Recycling R services");
        log.trace("Worker pool before return... " +
                "active = " + workerPool.getNumActive() + ", idle = " + workerPool.getNumIdle());
        try {
            if (rServices != null) {
                workerPool.returnObject(rServices);
                log.trace("Worker pool after return... " +
                        "active = " + workerPool.getNumActive() + ", idle = " + workerPool.getNumIdle());
            }
            else {
                log.warn("R services object became unexpectedly null, invalidating");
                workerPool.invalidateObject(rServices);
                workerPool.returnObject(rServices);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new AtlasRServicesException(
                    "Failed to release an RServices object back into the pool of workers", e);
        }
    }

    public synchronized void releaseResources() {
        log.debug("Releasing resources...");
        if (isInitialized) {
            try {
                if (workerPool.getNumActive() > 0) {
                    log.warn("Shutting down even though there are still some active compute workers");
                }

                workerPool.clear();
                workerPool.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                log.error("Problem shutting down compute service", e.getMessage());
            }
        }
    }

    /**
     * Lazily initializes the worker pool when the first R service is requested
     *
     * @throws AtlasRServicesException if initialization failed
     */
    private synchronized void initialize() throws AtlasRServicesException {
        if (!isInitialized) {
            if (validateEnvironment()) {
                // create worker pool
                workerPool = new GenericObjectPool(new RWorkerObjectFactory());
                workerPool.setMaxActive(32);
                workerPool.setMaxIdle(32);
                workerPool.setTestOnBorrow(true);
                workerPool.setTestOnReturn(true);

                isInitialized = true;
            }
            else {
                String msg = "Unable to initialize - R environment is not valid.  See log for details.";
                log.error(msg);
                throw new AtlasRServicesException(msg);
            }
        }
    }

    private class RWorkerObjectFactory implements PoolableObjectFactory {
        private final ServantProvider sp;

        public RWorkerObjectFactory() {
            // fixme: this is a hack to overcome hard-coded 8 worker limit from ServantProviderFactory
            hackServantProxyPoolSettings();

            sp = ServantProviderFactory.getFactory().getServantProvider();
        }

        public synchronized Object makeObject() throws Exception {
            log.debug("Attempting to create another rServices object for the pool...");
            RServices rServices = (RServices) sp.borrowServantProxy();

            if (null == rServices) {
                log.debug("Borrowed an rServices object that proved to be null");
                throw new NoSuchElementException();
            }

            log.debug("rServices acquired, registering logging/console reporting listeners");

            // add output listener
            rServices.addRConsoleActionListener(new MyRConsoleActionListener());
            MyRemoteLogListener listener = new MyRemoteLogListener();
            rServices.addOutListener(listener);
            rServices.addErrListener(listener);


            log.debug("Acquired biocep worker " + rServices.getServantName());
            return rServices;
        }

        public synchronized void destroyObject(Object o) throws Exception {
            RServices R = (RServices) o;
            log.debug("Released biocep worker " + R.getServantName());

            sp.returnServantProxy((RServices) o);
        }

        public synchronized boolean validateObject(Object o) {
            log.debug("Testing validity of " + o.toString() + "...");
            try {
                // check response to ping
                RServices rServices = (RServices) o;
                String pingResponse = rServices.ping();
                log.debug("Worker response to ping: " + pingResponse);

                // test this worker can evaluate 2 + 2
                boolean valid;
                try {
                    double[] values = ((RNumeric) rServices.getObject("2 + 2")).getValue();
                    if (values.length > 0) {
                        log.debug("Worker response to 2 + 2: " + values[0]);
                        valid = (values[0] == 4.0);
                    }
                    else {
                        log.debug("No response to 2 + 2 from worker");
                        valid = false;
                    }
                }
                catch (Exception e) {
                    log.error("R worker threw exception during validity test, invalidating");
                    valid = false;
                }

                if (!valid) {
                    log.warn("R worker " + rServices.getServantName() + " could not accurately evaluate 2 + 2 - " +
                            "this worker will be released");
                    // invalidate the worker if it's not valid
                    rServices.die();
                    return false;
                }
                else {
                    return true;
                }
            }
            catch (RemoteException e) {
                log.error("R worker does not respond to ping correctly ({}). Invalidated.", e.getMessage());
                return false;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public synchronized void activateObject(Object o) throws Exception {
        }

        public synchronized void passivateObject(Object o) throws Exception {
        }

        private void hackServantProxyPoolSettings() {
            // from set system properties
            String poolName, driver, url, user, password;
            poolName = System.getProperty("pools.dbmode.defaultpoolname");
            driver = System.getProperty("pools.dbmode.driver");
            user = System.getProperty("pools.dbmode.user");
            password = System.getProperty("pools.dbmode.password");

            String dbType = System.getProperty("pools.dbmode.type");
            String dbHost = System.getProperty("pools.dbmode.host");
            int dbPort = Integer.parseInt(System.getProperty("pools.dbmode.port"));
            String dbName = System.getProperty("pools.dbmode.name");

            if (dbType.equals("derby")) {
                url = "jdbc:derby://" + dbHost + ":" + dbPort + "/" + dbName + ";create=true";
                driver = "org.apache.derby.jdbc.ClientDriver";
            }
            else if (dbType.equals("mysql")) {
                url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
                driver = "org.gjt.mm.mysql.Driver";

            }
            else if (dbType.equals("oracle")) {
                url = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":" + dbName;
                driver = "oracle.jdbc.driver.OracleDriver";
            }
            else {
                throw new IllegalArgumentException("No biocep database URL can be extrapolated from the " +
                        "system properties defined");
            }

            ServantProxyPoolSingletonDB.getInstance(poolName, driver, url, user, password).setMaxActive(-1);
        }
    }


    public class MyRConsoleActionListener
            extends UnicastRemoteObject
            implements RConsoleActionListener, Serializable {
        public MyRConsoleActionListener() throws RemoteException {
            super();
        }

        public void rConsoleActionPerformed(RConsoleAction consoleAction) throws RemoteException {
            BiocepAtlasRFactory.this.log.trace(
                    "R console said:\n\t" + consoleAction.getAttributes().get("log"));
        }
    }

    public class MyRemoteLogListener
            extends UnicastRemoteObject
            implements RemoteLogListener, Serializable {
        public MyRemoteLogListener() throws RemoteException {
            super();
        }

        public void write(final byte[] b) throws RemoteException {
            BiocepAtlasRFactory.this.log.trace(new String(b));
        }

        public void write(final byte[] b, final int off, final int len) throws RemoteException {
            BiocepAtlasRFactory.this.log.trace(new String(b, off, len));
        }

        public void write(final int b) throws RemoteException {
            BiocepAtlasRFactory.this.log.trace(new String(new byte[]{(byte) b, (byte) (b >> 8)}));
        }

        public void flush() throws RemoteException {
        }
    }
}
