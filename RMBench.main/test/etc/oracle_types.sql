-- Oracle 2 types test
create table TYPES_TEST (
	id integer not null primary key,

	varchar2_test VARCHAR2(10),
	varchar2_byte_test VARCHAR2(10 BYTE),
	varchar2_char_test VARCHAR2(10 CHAR),
	nvarchar2_test NVARCHAR2(10),
	char_test CHAR,
	char_10_test CHAR(10),
	char_byte_test CHAR(10 BYTE),
	char_char_test VARCHAR2(10 CHAR),
	nchar_test NCHAR,
	nchar_10_test NCHAR(10),
	number_test NUMBER,
	number_1_test NUMBER(1),
	number_10_test NUMBER(10),
	number_10_2_test NUMBER(10, 2),
	long_test LONG,
	date_test DATE,
	binary_float_test BINARY_FLOAT,
	binary_double_test BINARY_DOUBLE,
	timestamp_test TIMESTAMP,
	timestamp_2_test TIMESTAMP(2),
	timestamp_tz_test TIMESTAMP WITH TIME ZONE,
	timestamp_tz2_test TIMESTAMP(2) WITH TIME ZONE,
	timestamp_ltz_test TIMESTAMP WITH LOCAL TIME ZONE,
	timestamp_ltz2_test TIMESTAMP(2) WITH LOCAL TIME ZONE,
	interval_ytom_test INTERVAL YEAR TO MONTH,
	interval_y4tom_test INTERVAL YEAR (4) TO MONTH,
	interval_dtos_test INTERVAL DAY TO SECOND,
	interval_d4tos8_test INTERVAL DAY (4) TO SECOND (8),
	raw_test RAW(100),
	rowid_test ROWID,
	urowid_test UROWID,
	urowid_200_test UROWID(200),
	clob_test CLOB,
	nclob_test NCLOB,
	blob_test BLOB,
	bfile_test BFILE
);

drop table TYPES_TEST2;

create table TYPES_TEST2 (
	id RAW(32) default SYS_GUID() not null primary key,
	longraw_test LONG RAW,
	varchar_test varchar(100),
	float_test float
);
