drop table test.small_TEST;

create table test.small_TEST (
id integer not null,
smallint_test SMALLINT,
int_test INTEGER,
bigint_test BIGINT,
real_test REAL,
float_test1 FLOAT,
float_test2 FLOAT(20),
double_test DOUBLE,
decimal_test1 DECIMAL,
decimal_test2 DECIMAL(10, 2),
char_test CHAR(30),varchar_test VARCHAR(100),longvarchar_test LONG VARCHAR,char_bitdata_test CHAR(10) FOR BIT DATA,varchar_bitdata_test VARCHAR(100) FOR BIT DATA,
longvarchar_bitdata_test LONG VARCHAR FOR BIT DATA,blob_test1 BLOB,blob_test2 BLOB(2M),
clob_test1 CLOB,clob_test2 CLOB(2M),
date_test DATE,time_test TIME,timestamp_test TIMESTAMP,

primary key(id)
);