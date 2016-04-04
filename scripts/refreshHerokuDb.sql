DROP TABLE "public"."offers_service";
DROP TABLE "public"."provides_service_to";
DROP TABLE "public"."works_in";
DROP TABLE "public"."located_at";
DROP TABLE "public"."officelocation";
DROP TABLE "public"."timeentry";
DROP TABLE "public"."ticks";
DROP TABLE "public"."employee";
DROP TABLE "public"."service";
DROP TABLE "public"."servicetype";
DROP TABLE "public"."client";
DROP TABLE "public"."department";

CREATE TABLE "client" (
    "cid" character(9) NOT NULL,
    "name" character(20) NOT NULL,
    "birthdate" "date",
    "homephonenumber" character(10),
    "workphonenumber" character(10),
    "streetaddr" character(20),
    "city" character(20),
    "province" character(2),
    "postalcode" character(6)
);
CREATE TABLE "department" (
    "dptname" character(20) NOT NULL,
    "dptphonenumber" character(10)
);
CREATE TABLE "employee" (
    "sin" character(9) NOT NULL,
    "name" character(20) NOT NULL,
    "workphonenumber" character(20),
    "homephonenumber" character(20),
    "streetaddress" character(20),
    "city" character(20),
    "postalcode" character(7),
    "reports_to" character(9)
);
CREATE TABLE "located_at" (
    "streetaddress" character(20) NOT NULL,
    "postalcode" character(7) NOT NULL,
    "dptname" character(20) NOT NULL
);
CREATE TABLE "offers_service" (
    "sin" character(10) NOT NULL,
    "servicetypename" character(20) NOT NULL
);
CREATE TABLE "officelocation" (
    "streetaddress" character(20) NOT NULL,
    "postalcode" character(7) NOT NULL,
    "city" character(20),
    "province" character(2),
    "mainofficenumber" character(10),
    "oid" character(5)
);
CREATE TABLE "provides_service_to" (
    "sin" character(9) NOT NULL,
    "cid" character(9) NOT NULL,
    "sid" character(10) NOT NULL
);
CREATE TABLE "service" (
    "sid" character(10) NOT NULL,
    "servicetypename" character(20) NOT NULL,
    "basefee" real NOT NULL,
    "hourlyrate" real NOT NULL,
    "amountpaid" real
);
CREATE TABLE "servicetype" (
    "servicetypename" character(20) NOT NULL
);
CREATE TABLE "ticks" (
    "tick" timestamp without time zone
);
CREATE TABLE "timeentry" (
    "sid" character(10) NOT NULL,
    "startdatetime" "date" NOT NULL,
    "enddatetime" "date" NOT NULL
);
CREATE TABLE "works_in" (
    "sin" character(9),
    "dptname" character(20)
);

ALTER TABLE "client"
    ADD CONSTRAINT "client_pkey" PRIMARY KEY ("cid");
ALTER TABLE "department"
    ADD CONSTRAINT "department_pkey" PRIMARY KEY ("dptname");
ALTER TABLE "employee"
    ADD CONSTRAINT "employee_pkey" PRIMARY KEY ("sin");
ALTER TABLE "located_at"
    ADD CONSTRAINT "located_at_pkey" PRIMARY KEY ("streetaddress", "postalcode", "dptname");
ALTER TABLE "offers_service"
    ADD CONSTRAINT "offers_service_pkey" PRIMARY KEY ("sin", "servicetypename");
ALTER TABLE "officelocation"
    ADD CONSTRAINT "officelocation_pkey" PRIMARY KEY ("streetaddress", "postalcode");
ALTER TABLE "provides_service_to"
    ADD CONSTRAINT "provides_service_to_pkey" PRIMARY KEY ("sin", "cid", "sid");
ALTER TABLE "service"
    ADD CONSTRAINT "service_pkey" PRIMARY KEY ("sid");
ALTER TABLE "servicetype"
    ADD CONSTRAINT "servicetype_pkey" PRIMARY KEY ("servicetypename");
ALTER TABLE "timeentry"
    ADD CONSTRAINT "timeentry_pkey" PRIMARY KEY ("sid", "startdatetime", "enddatetime");
ALTER TABLE "officelocation"
    ADD CONSTRAINT "unique_oid" UNIQUE ("oid");
ALTER TABLE "employee"
    ADD CONSTRAINT "employee_reports_to_fkey" FOREIGN KEY ("reports_to") REFERENCES "employee"("sin") ON UPDATE CASCADE;
ALTER TABLE "located_at"
    ADD CONSTRAINT "located_at_dptname_fkey" FOREIGN KEY ("dptname") REFERENCES "department"("dptname") ON UPDATE CASCADE;
ALTER TABLE "located_at"
    ADD CONSTRAINT "located_at_streetaddress_fkey" FOREIGN KEY ("streetaddress", "postalcode") REFERENCES "officelocation"("streetaddress", "postalcode") ON UPDATE CASCADE;
ALTER TABLE "offers_service"
    ADD CONSTRAINT "offers_service_servicetypename_fkey" FOREIGN KEY ("servicetypename") REFERENCES "servicetype"("servicetypename") ON UPDATE CASCADE;
ALTER TABLE "offers_service"
    ADD CONSTRAINT "offers_service_sid_fkey" FOREIGN KEY ("sin") REFERENCES "employee"("sin") ON UPDATE CASCADE;
ALTER TABLE "provides_service_to"
    ADD CONSTRAINT "provides_service_to_cid_fkey" FOREIGN KEY ("cid") REFERENCES "client"("cid") ON UPDATE CASCADE;
ALTER TABLE "provides_service_to"
    ADD CONSTRAINT "provides_service_to_sid_fkey" FOREIGN KEY ("sid") REFERENCES "service"("sid") ON UPDATE CASCADE;
ALTER TABLE "provides_service_to"
    ADD CONSTRAINT "provides_service_to_sin_fkey" FOREIGN KEY ("sin") REFERENCES "employee"("sin") ON UPDATE CASCADE;
ALTER TABLE "service"
    ADD CONSTRAINT "service_servicetypename_fkey" FOREIGN KEY ("servicetypename") REFERENCES "servicetype"("servicetypename") ON UPDATE CASCADE;
ALTER TABLE "timeentry"
    ADD CONSTRAINT "timeentry_sid_fkey" FOREIGN KEY ("sid") REFERENCES "service"("sid") ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE "works_in"
    ADD CONSTRAINT "works_in_dptname_fkey" FOREIGN KEY ("dptname") REFERENCES "department"("dptname");
ALTER TABLE "works_in"
    ADD CONSTRAINT "works_in_sin_fkey" FOREIGN KEY ("sin") REFERENCES "employee"("sin");

insert into client values ('000001031', 'fred', '1977-06-05', '1233244361', '1233244361', '1234 Main Street', 'Toronto', 'ON', 'A1A1A1');
insert into client values ('000001032', 'leslie korn', '1975-04-05', '7713842345', '1233244361', '1234 Main Street', 'Toronto', 'ON', 'A1A1A1');
insert into client values ('000001033', 'jackie chan', '1937-06-11', '7780001010', '7781230001', '101 Dark Corridor', 'Toronto', 'ON', '8N8N8N');
insert into client values ('000001035', 'john kim', '1991-10-11', '1112342345', '1112223334', '1234 Main Street', 'Toronto', 'ON', 'B2B2B2');
insert into client values ('400000005', 'Sean Wu', '1970-05-15', '6045652841', '7782788842', '295 E 52nd st', 'Vancouver', 'BC', 'V4V5R5');
insert into client values ('000011', 'Ibrahim Test', '2016-03-09', '7322', '732', '1301 9th Ave SW', 'Calgary', 'AB', 'T3C0H9');
insert into client values ('000001034', 'melissa melbourne', '1991-05-12', '1758646248', '1233244361', '1234 Main Street', 'Calgary', 'BC', 'C1B2A4');
insert into client values ('466471299', 'Jorge Lazo', '2016-04-03', '234234234', '7782880482', '472 W38th Ave', 'Vancouver', 'BC', 'V5Y2N6');

insert into department values ('government branch', 'ext-1');
insert into department values ('personal branch', 'ext-2');
insert into department values ('financial branch', 'ext-2');
insert into department values ('tax and audit branch', 'ext-3');
insert into department values ('criminal branch', 'ext-4');

insert into employee values ('000000001', 'John Casey', '6044503241', '', '653 W 32nd Ave', 'Vancouver', 'V6T 1W2', NULL);
insert into employee values ('000000002', 'Mary Proctor', '6044506793', '7782880953', '583 King Ave', 'Vancouver', 'V4P 2T6', '000000001');
insert into employee values ('000000003', 'Yan Li', '6044502803', '77828858204', '5830 Burrard st', 'Vancouver', 'V8Z 1W4', '000000001');
insert into employee values ('000000004', 'Paula Pino', '6044506820', '', '525 W 2nd Ave', 'Richmond', 'V6U 3F2', '000000003');
insert into employee values ('000000005', 'Nancy Duart', '60445085028', '7782885829', '299 W 28nd Ave', 'Vancouver', 'V63 1H2', '000000001');

insert into officelocation values ('2947 W4th Ave', 'V6K 1R3', 'Vancouver', 'BC', '6045551020', '00001');
insert into officelocation values ('1245 Nicola St', 'V6G 2Y6', 'Vancouver', 'BC', '6045551230', '00002');
insert into officelocation values ('3150 Blanca St', 'V6R 4G3', 'Vancouver', 'BC', '6045551110', '00003');
insert into officelocation values ('3175 Marine Dr', 'V7T 1B6', 'West Vancouver', 'BC', '6045552045', '00004');
insert into officelocation values ('9551 Patterson Rd', 'V6X 1P8', 'Richmond', 'BC', '6043002344', '00005');
insert into officelocation values ('1910 E 8th Ave', 'V5N 5M2', 'Vancouver', 'BC', '6045558902', '00006');

insert into located_at values ('2947 W4th Ave', 'V6K 1R3', 'government branch');
insert into located_at values ('2947 W4th Ave', 'V6K 1R3', 'personal branch');
insert into located_at values ('1245 Nicola St', 'V6G 2Y6', 'government branch');
insert into located_at values ('1245 Nicola St', 'V6G 2Y6', 'personal branch');
insert into located_at values ('1245 Nicola St', 'V6G 2Y6', 'financial branch');
insert into located_at values ('3150 Blanca St', 'V6R 4G3', 'criminal branch');
insert into located_at values ('1245 Nicola St', 'V6G 2Y6', 'tax and audit branch');

insert into servicetype values ('governmental');
insert into servicetype values ('personal');
insert into servicetype values ('financial');
insert into servicetype values ('government grants');
insert into servicetype values ('civil disputes');
insert into servicetype values ('taxes');
insert into servicetype values ('accounting services');
insert into servicetype values ('financial services');
insert into servicetype values ('audit and risk mgmt');
insert into servicetype values ('marriage');

insert into service values ('1000000001', 'government grants', '100', '30', '0');
insert into service values ('1000000002', 'civil disputes', '80', '20', '80');
insert into service values ('1000000003', 'government grants', '100', '30', '130');
insert into service values ('1000000004', 'audit and risk mgmt', '70', '40', '150');
insert into service values ('1000000005', 'financial services', '60', '20', '0');
insert into service values ('2000000000', 'civil disputes', '100', '50', '0');
insert into service values ('2000000001', 'marriage', '100', '50', '0');
insert into service values ('2000000002', 'civil disputes', '100', '50', '0');

insert into ticks values ('2016-03-11 19:44:15.216761');
insert into ticks values ('2016-03-12 19:55:52.76498');
insert into ticks values ('2016-03-12 12:23:52.382736');

insert into works_in values ('000000001', 'government branch');
insert into works_in values ('000000002', 'government branch');
insert into works_in values ('000000003', 'personal branch');
insert into works_in values ('000000001', 'personal branch');
insert into works_in values ('000000001', 'financial branch');
insert into works_in values ('000000004', 'financial branch');
insert into works_in values ('000000005', 'financial branch');

insert into offers_service values ('000000001', 'government grants');
insert into offers_service values ('000000001', 'civil disputes');
insert into offers_service values ('000000001', 'taxes');
insert into offers_service values ('000000002', 'government grants');
insert into offers_service values ('000000003', 'marriage');
insert into offers_service values ('000000004', 'audit and risk mgmt');
insert into offers_service values ('000000005', 'financial services');
insert into offers_service values ('000000005', 'civil disputes');
insert into offers_service values ('000000002', 'taxes');
insert into offers_service values ('000000004', 'marriage');

insert into provides_service_to values ('000000001', '000001032', '1000000001');
insert into provides_service_to values ('000000002', '000001034', '1000000002');
insert into provides_service_to values ('000000001', '000001032', '1000000003');
insert into provides_service_to values ('000000004', '400000005', '1000000005');
insert into provides_service_to values ('000000001', '000001031', '2000000000');
insert into provides_service_to values ('000000003', '400000005', '2000000001');
insert into provides_service_to values ('000000004', '466471299', '2000000002');

insert into timeentry values ('1000000001', '2015-01-05', '2015-01-15');
insert into timeentry values ('1000000002', '2015-02-05', '2015-03-15');
insert into timeentry values ('1000000003', '2015-01-10', '2015-01-10');
insert into timeentry values ('1000000004', '2015-02-08', '2015-01-10');
insert into timeentry values ('1000000005', '2015-01-24', '2015-01-25');
insert into timeentry values ('1000000001', '2015-03-05', '2015-03-15');
