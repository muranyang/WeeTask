DROP TABLE TASK CASCADE CONSTRAINTS;

CREATE TABLE TASK
(
  TASK_ID      INTEGER NOT NULL,
  TASK_NAME    VARCHAR2(30 BYTE),
  ASSIGNEE     VARCHAR2(30 BYTE),
  DESCRIPTION  VARCHAR2(150 BYTE),
  D_DATE       DATE
);

create sequence ID_SEQ
increment by 1
start with 1
nomaxvalue
nocycle
nocache
order;

commit;