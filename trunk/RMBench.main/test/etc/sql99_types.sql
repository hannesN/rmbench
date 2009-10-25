-- all SQL99 data types
-- =====================
create table SQL99_TEST (
	integer_test integer,
	int_test int,
	smallint_test smallint,
	numeric_test numeric
	numeric_10_2_test numeric(10, 2),
	decimal_test decimal,
	dec_test dec,
	decimal_10_2_test decimal(10, 2),
	float_test float,
	real_test real,
	double_rec_test double precision,
	double_test double,
	bit_test bit,
	varbit_test bit varying,
	blob1_test binary large object,
	blob2_test blob,
	character_test character,
	char_test char,
	char_100_test char(100),
	varchar1_100_test character varying(100),
	varchar2_100_test varchar(100),
	nchar1_test national character,
	nchar2_test national char,
	nchar3_test nchar,
	national character varying,
	nvarchar1_100_test national char varying(100),
	nvarchar2_100_test nchar varying(100),
	clob1_test character larget object,
	clob2_test clob,
	nclob1_test national character large object,
	nclob2_test nclob,
	date_test date,
	time_test time,
	timetz_test time with time zone,
	timestamp_test timestamp,
	timestamptz_test timestamp with time zone,
	interval_test interval,
	boolean_test boolean
)