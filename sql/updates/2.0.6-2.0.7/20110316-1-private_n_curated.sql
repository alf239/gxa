alter table A2_EXPERIMENT add curated NUMBER(1) DEFAULT 0;
alter table A2_EXPERIMENT add private NUMBER(1) DEFAULT 1;
update A2_EXPERIMENT set curated = 1, private = 0;