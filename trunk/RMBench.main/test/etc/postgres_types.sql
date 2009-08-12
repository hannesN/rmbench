-- create a table for type tests
-- =============================
create table test2."TYPES_TEST" (
	bigint_test bigint, 				-- int8
	int_test integer,					-- int, int4	signed four-byte integer
	smallint_test smallint,				-- int2	signed two-byte integer

	double_test double precision,		-- float8	double precision floating-point number
	real_test real,						-- float4	single precision floating-point number

	money_test money,					-- currency amount
	numeric_test numeric,				-- decimal [ (p, s) ]	exact numeric of selectable precision
	numeric_10_2_test numeric(10,2),
	
	varchar_test character varying,		-- varchar [ (n) ]	variable-length character string
	varchar_100_test character varying(100),
	char_test character,				-- char [ (n) ]	fixed-length character string
	char_100_test character(100),
	text_test text,						-- variable-length character string

	bigserial_test bigserial, 			-- serial8	autoincrementing eight-byte integer
	serial_test serial,					-- serial4	autoincrementing four-byte integer

	bit_test bit, 						-- fixed-length bit string
	bit_100_test bit(100),
	varbit_test bit varying, 			-- varbit	variable-length bit string
	varbit_100_test bit varying(100),

	boolean_test boolean, 				-- bool	logical Boolean (true/false)

	bytea_test bytea,					-- binary data ("byte array")

	date_test date,						-- calendar date (year, month, day)
	time_test time,						-- [ (p) ] [ without time zone ]	 	time of day
	time_6_test time(6),
	timetz_test time with time zone,	-- timetz	time of day, including time zone
	
	timestamp_test timestamp,			-- [ (p) ] [ without time zone ]	 	date and time
	timestamp_6_test timestamp(6),
	timestamptz_test timestamp with time zone, -- timestamptz	date and time, including time zone

	interval_test interval,				-- time span
	interval_6_test interval(6),

	box_test box,						-- rectangular box in the plane
	circle_test circle,					-- circle in the plane
	line_test line, 					-- infinite line in the plane
	lseg_test lseg, 					-- line segment in the plane
	path_test path, 					-- geometric path in the plane
	point_test point,					-- geometric point in the plane
	polygon_test polygon, 				-- closed geometric path in the plane

	inet_test inet, 					-- IPv4 or IPv6 host address
	cidr_test cidr, 					-- IPv4 or IPv6 network address
	macaddr_test macaddr 				-- MAC address
)

