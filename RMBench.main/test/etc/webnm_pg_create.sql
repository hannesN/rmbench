-- test model WebNM - (postgresql)
-- author: Thomas Proeger
-- this database structure can be found at http://answerbook.somix.com/webnm/db_schema.html
-- 
-- supported database modeling features:
--  * multiple schemas 
--  * constraints:	Primary Key, Not null, foreign key, check
--  * n-m-table (webtts.qmembers)
--

--create schemas
CREATE SCHEMA crm;
CREATE SCHEMA logalot;
CREATE SCHEMA stivity;
CREATE SCHEMA perfrpt;
CREATE SCHEMA security;
CREATE SCHEMA webnm;
CREATE SCHEMA webtts;

-- create all tables
CREATE TABLE crm.individuals (
  customer_id integer PRIMARY KEY,
  added_by text,
  company_id text,
  salutation text,
  first_name text,
  last_name text,
  position text,
  email_address text,
  phone text,
  fax text,
  internal_notes text,
  C1 text,
  C2 text,
  C3 text,
  C4 text,
  C5 text
);
CREATE TABLE crm.organizations (
  company_id integer PRIMARY KEY,
  added_by text,
  company_name text,
  department text,
  division text,
  address1 text,
  address2 text,
  city_town text,
  state_prov text,
  zip_postal text,
  country text,
  phone text,
  fax text,
  internal_notes text,
  C1 text,
  C2 text,
  C3 text,
  C4 text,
  C5 text
);
CREATE TABLE crm.call_history (
  history_id integer PRIMARY KEY,
  reference_id integer,
  added_by text,
  minutes text,
  notes text,
  "time" integer,
  "type" text,
  customer_id integer,
  company_id text
);
CREATE TABLE logalot.bulletin_board (
  "id" integer PRIMARY KEY,
  policy_id integer NOT NULL,
  timestamp varchar(15) NOT NULL,
  epoch integer NOT NULL,
  source_address varchar(15) NOT NULL,
  protocol varchar(10) NOT NULL,
  alert_level varchar(20) NOT NULL,
  bboard_name varchar(100) NOT NULL,
  message text NOT NULL,
  CONSTRAINT uns_policy_id CHECK (policy_id > 0),
  CONSTRAINT uns_epoch CHECK (epoch > 0)
);
CREATE TABLE logalot.bulletin_board_header (
  id integer PRIMARY KEY,
  policy_id integer NOT NULL,
  occurrences integer NOT NULL,
  notifications_sent smallint NOT NULL,
  last_notifications  varchar(25) NOT NULL,
  treshold smallint NOT NULL,
  epoch_first integer NOT NULL,
  epoch_last integer NOT NULL,
  assigned_to varchar(35) NOT NULL,
  bboard_name varchar(100) NOT NULL,
  CONSTRAINT uns_policy_id CHECK (policy_id > 0),
  CONSTRAINT uns_occurrences CHECK (occurrences > 0),
  CONSTRAINT uns_notifications_sent CHECK (notifications_sent > 0),
  CONSTRAINT uns_treshold CHECK (treshold > 0),
  CONSTRAINT uns_epoch_first CHECK (epoch_first > 0),
  CONSTRAINT uns_epoch_last CHECK (epoch_last > 0)
);
CREATE TABLE logalot.eventlog_history (
  id integer PRIMARY KEY,
  policy_id integer NOT NULL,
  event_id integer NOT NULL,
  log_file varchar(20) NOT NULL,
  computer_name varchar(25) NOT NULL,
  username varchar(35) NOT NULL,
  "source_name" varchar(30) NOT NULL,
  category varchar(20) NOT NULL,
  event_type varchar(15) NOT NULL,
  time_generated integer NOT NULL,
  timestamp varchar(24) NOT NULL,
  message text NOT NULL
);
CREATE TABLE logalot.eventlog_setup (
  id integer PRIMARY KEY,
  machine varchar(50) NOT NULL,
  username varchar(35) NOT NULL,
  password varchar(40) NOT NULL,
  log_name varchar(20) NOT NULL,
  last_time integer NOT NULL,
  created_by varchar(100) NOT NULL,
  create_time integer NOT NULL,
  last_scan_time integer NOT NULL
);
CREATE TABLE logalot.history (
  id integer PRIMARY KEY,
  policy_id integer NOT NULL,
  timestamp varchar(24) NOT NULL,
  epoch integer NOT NULL,
  source_address varchar(15) NOT NULL,
  alert_level varchar(20) NOT NULL,
  protocol varchar(10) NOT NULL,
  message text NOT NULL
);
CREATE TABLE logalot.email_history (
  id integer PRIMARY KEY,
  source varchar(15) NOT NULL,
  type varchar(15) NOT NULL,
  local_srv varchar(15) NOT NULL,
  remote_srv varchar(15) NOT NULL,
  handle varchar(35) NOT NULL,
  helo text NOT NULL,
  from_user varchar(100) NOT NULL,
  from_domain text NOT NULL,
  to_user varchar(100) NOT NULL,
  to_domain text NOT NULL,
  spool_file varchar(21) NOT NULL,
  size integer NOT NULL,
  extra text NOT NULL,
  to_address varchar(100) NOT NULL,
  from_address varchar(100) NOT NULL,
  timestamp varchar(24) NOT NULL,
  epoch integer NOT NULL,
  delivery boolean NOT NULL,
  spam boolean NOT NULL,
  virus boolean NOT NULL
);
CREATE TABLE logalot.notify (
  id integer PRIMARY KEY,
  group_id integer NOT NULL,
  name varchar(100) NOT NULL,
  action varchar(30) NOT NULL,
  arguments text NOT NULL,
  priority integer NOT NULL,
  minutes_trigger integer NOT NULL,
  status boolean NOT NULL
);
CREATE TABLE logalot.orphan (
  id integer PRIMARY KEY,
  timestamp varchar(24) NOT NULL,
  epoch integer  NOT NULL,
  source_address varchar(15)  NOT NULL,
  alert_level varchar(20)  NOT NULL,
  protocol varchar(10)  NOT NULL,
  message text  NOT NULL
);
CREATE TABLE logalot.policies (
  id integer PRIMARY KEY,
  name varchar(100) NOT NULL,
  source_address_filter varchar(20) NOT NULL,
  alert_level_filter varchar(20) NOT NULL,
  protocol_filter varchar(10) NOT NULL,
  message text NOT NULL,
  action varchar(100) NOT NULL,
  color varchar(25) NOT NULL,
  treshold integer NOT NULL,
  notification varchar(100) NOT NULL,
  notify_always boolean NOT NULL,
  priority integer NOT NULL,
  notes text NOT NULL,
  creation_info varchar(150) NOT NULL,
  exclude varchar(255) NOT NULL,
  range_begin varchar(15) NOT NULL,
  range_end varchar(15) NOT NULL,
  rate_count integer NOT NULL,
  rate_time integer NOT NULL
);
CREATE TABLE logalot.report_settings (
  id integer PRIMARY KEY,
  variable varchar(50) NOT NULL,
  value varchar(100) NOT NULL,
  username varchar(50) NOT NULL,
  description text NOT NULL
);
CREATE TABLE logalot.reports (
  id integer PRIMARY KEY,
  name varchar(200) NOT NULL,
  query text NOT NULL,
  start_epoch integer NOT NULL,
  end_epoch integer NOT NULL,
  table_type varchar(20) NOT NULL,
  report_type boolean NOT NULL
);
CREATE TABLE stivity.asset_management (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  custom1 text NOT NULL,
  custom2 text NOT NULL,
  comments text NOT NULL,
  custom3 text NOT NULL,
  custom4 text NOT NULL,
  custom5 text NOT NULL,
  custom6 text NOT NULL,
  custom7 text NOT NULL,
  custom8 text NOT NULL,
  custom9 text NOT NULL,
  barcode text NOT NULL,
  mac_address varchar(30) NOT NULL default '',
  asset_type varchar(200) NOT NULL default '',
  custom10 text NOT NULL,
  custom11 text NOT NULL,
  custom12 text NOT NULL,
  custom13 text NOT NULL,
  custom14 text NOT NULL,
  custom15 text NOT NULL,
  custom16 text NOT NULL,
  custom17 text NOT NULL,
  custom18 text NOT NULL,
  custom19 text NOT NULL,
  custom20 text NOT NULL,
  custom21 text NOT NULL,
  custom22 text NOT NULL,
  custom23 text NOT NULL,
  custom24 text NOT NULL,
  custom25 text NOT NULL,
  custom26 text NOT NULL,
  custom27 text NOT NULL,
  custom28 text NOT NULL,
  custom29 text NOT NULL,
  custom30 text NOT NULL
);
CREATE TABLE stivity.baseboard (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(25) NOT NULL default '',
  config_options text NOT NULL,
  depth integer NOT NULL default '0',
  description varchar(25) NOT NULL default '',
  height integer NOT NULL default '0',
  hosting_board char(3) NOT NULL default '0',
  hot_swappable char(3) NOT NULL default '0',
  install_date varchar(20) NOT NULL default '',
  manufacturer varchar(50) NOT NULL default '',
  model varchar(50) NOT NULL default '',
  name varchar(25) NOT NULL default '',
  other_identifying_info varchar(200) NOT NULL default '',
  part_number varchar(100) NOT NULL default '',
  product varchar(100) NOT NULL default '',
  requirements_description varchar(100) NOT NULL default '',
  requires_daughter_board char(3) NOT NULL default '0',
  serial_number varchar(100) NOT NULL default '',
  sku varchar(50) NOT NULL default '',
  slot_layout varchar(200) NOT NULL default '',
  status varchar(25) NOT NULL default '',
  tag varchar(25) NOT NULL default '',
  version varchar(50) NOT NULL default '',
  weight smallint NOT NULL default '0',
  width integer NOT NULL default '0'
);
CREATE TABLE stivity.battery (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(25) NOT NULL default '',
  battery_recharge_time smallint NOT NULL default '0',
  battery_status varchar(25) NOT NULL default '',
  caption varchar(75) NOT NULL default '',
  chemistry varchar(30) NOT NULL default '',
  description varchar(75) NOT NULL default '',
  design_capacity smallint NOT NULL default '0',
  design_voltage decimal(6,5) NOT NULL default '0.00000',
  device_id varchar(50) NOT NULL default '',
  estimated_charge_remaining integer NOT NULL default '0',
  estimated_run_time integer NOT NULL default '0',
  expected_battery_life integer NOT NULL default '0',
  expected_life integer NOT NULL default '0',
  full_charge_capacity integer NOT NULL default '0',
  install_date varchar(25) NOT NULL default '',
  max_recharge_time integer NOT NULL default '0',
  name varchar(75) NOT NULL default '',
  smart_battery_version varchar(10) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  system_name varchar(100) NOT NULL default '',
  time_on_battery integer NOT NULL default '0',
  time_to_full_charge integer NOT NULL default '0'
); 
CREATE TABLE stivity.bios (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  bios_version varchar(150) NOT NULL default '',
  caption varchar(200) NOT NULL default '',
  build_number varchar(50) NOT NULL default '',
  bios_characteristics text NOT NULL,
  current_language varchar(40) NOT NULL default '',
  description varchar(200) NOT NULL default '',
  install_date varchar(25) NOT NULL default '',
  name varchar(200) NOT NULL default '',
  manufacturer varchar(75) NOT NULL default '',
  primary_bios char(3) NOT NULL default '0',
  release_date varchar(50) NOT NULL default '',
  serial_number varchar(150) NOT NULL default '',
  smbios_version varchar(50) NOT NULL default '',
  smbios_major_version smallint NOT NULL default '0',
  smbios_minor_version decimal(6,2) NOT NULL default '0.00',
  smbios_present char(3) NOT NULL default '',
  software_element_id varchar(200) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  version varchar(150) NOT NULL default ''
);
CREATE TABLE stivity.bus (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(40) NOT NULL default '',
  caption varchar(25) NOT NULL default '',
  description varchar(25) NOT NULL default '',
  device_id varchar(50) NOT NULL default '',
  name varchar(25) NOT NULL default '',
  pnp_device_id varchar(50) NOT NULL default '',
  status varchar(15) NOT NULL default ''
);
CREATE TABLE stivity.cdrom (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  capabilities text NOT NULL,
  caption varchar(50) NOT NULL default '',
  compression_method varchar(100) NOT NULL default '',
  default_blockSize integer  NOT NULL default '0',
  description varchar(50) NOT NULL default '',
  device_id varchar(150) NOT NULL default '',
  drive varchar(40) NOT NULL default '',
  error_methodology varchar(100) NOT NULL default '',
  id varchar(40) NOT NULL default '',
  manufacturer varchar(50) NOT NULL default '',
  max_block_size integer  NOT NULL default '0',
  maximum_component_length integer  NOT NULL default '0',
  max_media_size integer  NOT NULL default '0',
  media_loaded char(3) NOT NULL default '',
  media_type varchar(50) NOT NULL default '',
  mfr_assigned_revision_level varchar(25) NOT NULL default '',
  name varchar(50) NOT NULL default '',
  needs_cleaning char(3) NOT NULL default '',
  revision_level varchar(50) NOT NULL default '',
  scsi_bus integer  NOT NULL default '0',
  scsi_logical_unit integer  NOT NULL default '0',
  scsi_port integer  NOT NULL default '0',
  scsi_target_id integer  NOT NULL default '0',
  size real  NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  transfer_rate real NOT NULL default '0',
  volume_name varchar(200) NOT NULL default '',
  volume_serial_number varchar(100) NOT NULL default ''
);
CREATE TABLE stivity.change_log (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  table_field varchar(75) NOT NULL default '',
  description text NOT NULL,
  new_val text NOT NULL,
  old_val text NOT NULL,
  new_time integer  NOT NULL default '0',
  old_time integer  NOT NULL default '0'
);
CREATE TABLE stivity.computer_system (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(75) NOT NULL default '',
  manufacturer varchar(50) NOT NULL default '',
  model varchar(100) NOT NULL default '',
  domain varchar(100) NOT NULL default '',
  domain_role varchar(50) NOT NULL default '',
  number_of_processors integer NOT NULL default '0',
  thermal_state varchar(25) NOT NULL default '',
  power_state varchar(35) NOT NULL default '',
  admin_password_status varchar(25) NOT NULL default '',
  bootup_state varchar(35) NOT NULL default '',
  chassis_bootup_state varchar(25) NOT NULL default '',
  daylight_in_effect char(3) NOT NULL default '',
  description varchar(150) NOT NULL default '',
  front_panel_reset_status varchar(25) NOT NULL default '',
  infrared_supported char(3) NOT NULL default '',
  name varchar(75) NOT NULL default '',
  network_server_mode_enabled char(3) NOT NULL default '',
  oem_string_array text NOT NULL,
  part_of_domain char(3) NOT NULL default '',
  power_on_password_status varchar(25) NOT NULL default '',
  power_supply_state varchar(35) NOT NULL default '',
  primary_owner_contact varchar(100) NOT NULL default '',
  primary_owner_name varchar(100) NOT NULL default '',
  reset_capability varchar(35) NOT NULL default '',
  roles varchar(150) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  support_contact_description varchar(200) NOT NULL default '',
  system_startup_options text NOT NULL,
  system_type varchar(150) NOT NULL default '',
  username varchar(100) NOT NULL default '',
  wakeup_type varchar(45) NOT NULL default '',
  workgroup varchar(75) NOT NULL default ''
);
CREATE TABLE stivity.disk_drive (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  bytes_per_sector smallint NOT NULL default '0',
  capabilities text NOT NULL,
  caption varchar(100) NOT NULL default '',
  compression_method varchar(50) NOT NULL default '',
  default_block_size integer NOT NULL default '0',
  description varchar(200) NOT NULL default '',
  device_id varchar(100) NOT NULL default '',
  error_methodology varchar(50) NOT NULL default '',
  indexing smallint NOT NULL default '0',
  interface_type varchar(50) NOT NULL default '',
  manufacturer varchar(75) NOT NULL default '',
  max_media_size integer NOT NULL default '0',
  media_type varchar(75) NOT NULL default '',
  model varchar(75) NOT NULL default '',
  name varchar(100) NOT NULL default '',
  partitions smallint NOT NULL default '0',
  pnp_device_id varchar(200) NOT NULL default '',
  scsi_bus integer NOT NULL default '0',
  scsi_logical_unit integer NOT NULL default '0',
  scsi_port integer NOT NULL default '0',
  scsi_target_id integer NOT NULL default '0',
  sectors_per_track smallint NOT NULL default '0',
  signature varchar(50) NOT NULL default '',
  size float NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  total_cylinders smallint NOT NULL default '0',
  total_heads integer NOT NULL default '0',
  total_sectors integer NOT NULL default '0',
  total_tracks integer NOT NULL default '0',
  tracks_per_cylinder integer NOT NULL default '0'
);
CREATE TABLE stivity.environment (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption text NOT NULL,
  description text NOT NULL,
  name varchar(150) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  system_variable char(3) NOT NULL default '',
  username varchar(100) NOT NULL default '',
  variable_value text NOT NULL
);
CREATE TABLE stivity.fan (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  active_cooling integer NOT NULL default '0',
  availability varchar(75) NOT NULL default '',
  caption varchar(100) NOT NULL default '',
  description varchar(100) NOT NULL default '',
  desired_speed smallint NOT NULL default '0',
  device_id varchar(75) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  variable_speed integer NOT NULL default '0'
);
CREATE TABLE stivity.floppy_controller (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(75) NOT NULL default '',
  description varchar(75) NOT NULL default '',
  device_id varchar(50) NOT NULL default '',
  manufacturer varchar(75) NOT NULL default '',
  status varchar(15) NOT NULL default ''
);
CREATE TABLE stivity.floppy_drive (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(75) NOT NULL default '',
  caption varchar(75) NOT NULL default '',
  description varchar(75) NOT NULL default '',
  device_id varchar(100) NOT NULL default '',
  manufacturer varchar(75) NOT NULL default '',
  status varchar(15) NOT NULL default ''
);
CREATE TABLE stivity.ide_controller (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(100) NOT NULL default '',
  manufacturer varchar(50) NOT NULL default '',
  max_number_controlled integer NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  device_id varchar(100) NOT NULL default ''
);
CREATE TABLE stivity.keyboard (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(75) NOT NULL default '',
  description varchar(150) NOT NULL default '',
  device_id varchar(75) NOT NULL default '',
  layout varchar(75) NOT NULL default '',
  number_of_function_keys integer NOT NULL default '0',
  status varchar(15) NOT NULL default ''
);
CREATE TABLE stivity.logical_disk (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(50) NOT NULL default '',
  compressed char(3) NOT NULL default '0',
  description varchar(70) NOT NULL default '',
  device_id varchar(100) NOT NULL default '',
  drive_type varchar(40) NOT NULL default '',
  file_system varchar(15) NOT NULL default '',
  free_space real NOT NULL default '0',
  maximum_component_length smallint NOT NULL default '0',
  media_type varchar(100) NOT NULL default '',
  name varchar(50) NOT NULL default '',
  provider_name varchar(150) NOT NULL default '',
  purpose varchar(100) NOT NULL default '',
  quotas_disabled char(3) NOT NULL default '0',
  quotas_incomplete char(3) NOT NULL default '0',
  quotas_rebuilding char(3) NOT NULL default '0',
  size real NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  supports_disk_quotas char(3) NOT NULL default '0',
  supports_file_based_compression char(3) NOT NULL default '0',
  volume_dirty char(3) NOT NULL default '0',
  volume_name varchar(150) NOT NULL default '',
  volume_serial_number varchar(100) NOT NULL default ''
);
CREATE TABLE stivity.monitor (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(75) NOT NULL default '',
  caption varchar(75) NOT NULL default '',
  description varchar(75) NOT NULL default '',
  device_id varchar(50) NOT NULL default '',
  monitor_manufacturer varchar(50) NOT NULL default '',
  monitor_type varchar(75) NOT NULL default '',
  name varchar(75) NOT NULL default '',
  pixels_per_x_logical_inch smallint NOT NULL default '0',
  pixels_per_y_logical_inch smallint NOT NULL default '0',
  pnp_device_id varchar(100) NOT NULL default '',
  screen_height smallint NOT NULL default '0',
  screen_width smallint NOT NULL default '0',
  status varchar(15) NOT NULL default ''
);
CREATE TABLE stivity.motherboard (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  description varchar(25) NOT NULL default '',
  caption varchar(25) NOT NULL default '',
  name varchar(25) NOT NULL default '',
  system_name varchar(75) NOT NULL default '',
  device_id varchar(25) NOT NULL default '',
  install_date varchar(25) NOT NULL default '',
  primary_bus_type varchar(25) NOT NULL default '',
  revision_number varchar(50) NOT NULL default '',
  secondary_bus_type varchar(25) NOT NULL default ''
);
CREATE TABLE stivity.network_adapter (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  adapter_type varchar(50) NOT NULL default '',
  auto_sense char(3) NOT NULL default '0',
  availability varchar(75) NOT NULL default '',
  caption varchar(150) NOT NULL default '',
  description varchar(150) NOT NULL default '',
  device_id varchar(25) NOT NULL default '',
  indexing integer NOT NULL default '0',
  install_date varchar(35) NOT NULL default '',
  installed char(3) NOT NULL default '0',
  mac_address varchar(20) NOT NULL default '',
  manufacturer varchar(40) NOT NULL default '',
  max_number_controlled integer NOT NULL default '0',
  max_speed integer NOT NULL default '0',
  name varchar(150) NOT NULL default '',
  net_connection_id varchar(75) NOT NULL default '',
  net_connection_status varchar(40) NOT NULL default '',
  network_addresses text NOT NULL,
  permanent_address varchar(100) NOT NULL default '',
  pnp_device_id varchar(100) NOT NULL default '',
  product_name varchar(75) NOT NULL default '',
  service_name varchar(25) NOT NULL default '',
  speed integer NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  time_of_last_reset varchar(35) NOT NULL default ''
);
CREATE TABLE stivity.network_adapter_configuration (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(150) NOT NULL default '',
  database_path varchar(100) NOT NULL default '',
  default_ip_gateway varchar(20) NOT NULL default '',
  default_tos integer NOT NULL default '0',
  default_ttl integer NOT NULL default '0',
  description varchar(150) NOT NULL default '',
  dhcp_enabled char(3) NOT NULL default '',
  dhcp_lease_expires varchar(35) NOT NULL default '',
  dhcp_lease_obtained varchar(35) NOT NULL default '',
  dhcp_server varchar(15) NOT NULL default '',
  dns_domain varchar(50) NOT NULL default '',
  dns_domain_suffix_search_order varchar(50) NOT NULL default '',
  dns_enabled_for_wins_resolution char(3) NOT NULL default '0',
  dns_host_name varchar(100) NOT NULL default '',
  dns_server_search_order varchar(150) NOT NULL default '',
  domain_dns_registration_enabled char(3) NOT NULL default '0',
  forward_buffer_memory smallint NOT NULL default '0',
  full_dns_registration_enabled char(3) NOT NULL default '0',
  indexing integer NOT NULL default '0',
  ip_address varchar(20) NOT NULL default '',
  ip_connection_metric integer NOT NULL default '0',
  ip_enabled char(3) NOT NULL default '0',
  ip_filter_security_enabled char(3) NOT NULL default '0',
  ip_port_security_enabled char(3) NOT NULL default '0',
  ip_sec_permit_ip_protocols char(3) NOT NULL default '0',
  ip_sec_permit_tcp_ports char(3) NOT NULL default '0',
  ip_sec_permit_udp_ports char(3) NOT NULL default '0',
  ip_subnet varchar(20) NOT NULL default '',
  ip_use_zero_broadcast char(3) NOT NULL default '0',
  ipx_address varchar(100) NOT NULL default '',
  ipx_enabled char(3) NOT NULL default '0',
  ipx_frame_type varchar(100) NOT NULL default '',
  ipx_media_type varchar(100) NOT NULL default '',
  ipx_network_number varchar(100) NOT NULL default '',
  ipx_virtual_net_number varchar(100) NOT NULL default '',
  keep_alive_interval smallint NOT NULL default '0',
  keep_alive_time smallint NOT NULL default '0',
  mac_address varchar(20) NOT NULL default '',
  mtu smallint NOT NULL default '0',
  num_forward_packets smallint NOT NULL default '0',
  pmtubh_detect_enabled char(3) NOT NULL default '0',
  pmtu_discovery_enabled char(3) NOT NULL default '0',
  service_name varchar(25) NOT NULL default '',
  setting_id varchar(75) NOT NULL default '',
  tcp_netbios_options varchar(20) NOT NULL default '',
  tcp_max_connect_retransmissions smallint NOT NULL default '0',
  tcp_max_data_retransmissions smallint NOT NULL default '0',
  tcp_num_connections smallint NOT NULL default '0',
  tcp_use_rfc1122_urgent_pointer char(3) NOT NULL default '0',
  tcp_window_size smallint NOT NULL default '0',
  wins_enable_lmhosts_lookup char(3) NOT NULL default '0',
  wins_host_lookup_file varchar(100) NOT NULL default '',
  wins_primary_server varchar(20) NOT NULL default '',
  wins_scope_id varchar(50) NOT NULL default '',
  wins_secondary_server varchar(20) NOT NULL default ''
);
CREATE TABLE stivity.onboard_device (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(100) NOT NULL default '',
  description varchar(100) NOT NULL default '',
  device_type varchar(25) NOT NULL default '',
  enabled char(3) NOT NULL default '0',
  hot_swappable char(3) NOT NULL default '0',
  manufacturer varchar(50) NOT NULL default '',
  model varchar(50) NOT NULL default '',
  name varchar(100) NOT NULL default '',
  other_identifying_info varchar(100) NOT NULL default '',
  part_number varchar(25) NOT NULL default '',
  removable char(3) NOT NULL default '0',
  replaceable char(3) NOT NULL default '0',
  serial_number varchar(25) NOT NULL default '',
  sku varchar(35) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  tag varchar(40) NOT NULL default '',
  version varchar(25) NOT NULL default ''
);
CREATE TABLE stivity.operating_system (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(75) NOT NULL default '',
  version varchar(25) NOT NULL default '',
  service_pack varchar(25) NOT NULL default '',
  groupname varchar(50) NOT NULL default '',
  serial_number varchar(45) NOT NULL default '',
  registered_user varchar(50) NOT NULL default '',
  organization varchar(50) NOT NULL default '',
  number_of_licensed_users smallint NOT NULL default '0',
  number_of_users smallint NOT NULL default '0',
  free_physical_memory float NOT NULL default '0',
  free_space_in_paging_files float NOT NULL default '0',
  boot_device varchar(50) NOT NULL default '',
  build_number varchar(25) NOT NULL default '',
  build_type varchar(25) NOT NULL default '',
  country_code smallint NOT NULL default '0',
  cs_name varchar(75) NOT NULL default '',
  current_time_zone varchar(50) NOT NULL default '',
  description text NOT NULL,
  distributed char(3) NOT NULL default '0',
  encryption_level smallint NOT NULL default '0',
  foreground_application_boost varchar(25) NOT NULL default '',
  free_virtual_memory float NOT NULL default '0',
  install_date varchar(35) NOT NULL default '',
  last_bootup_time varchar(35) NOT NULL default '',
  local_date_time varchar(35) NOT NULL default '',
  locale varchar(50) NOT NULL default '',
  manufacturer varchar(40) NOT NULL default '',
  max_process_memory_size float NOT NULL default '0',
  name varchar(125) NOT NULL default '',
  number_of_processes smallint NOT NULL default '0',
  os_type varchar(35) NOT NULL default '',
  plus_product_id varchar(35) NOT NULL default '',
  plus_version_number varchar(35) NOT NULL default '',
  is_primary char(3) NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  system_device varchar(50) NOT NULL default '',
  system_directory varchar(30) NOT NULL default '',
  system_drive varchar(15) NOT NULL default '',
  total_physical_memory float NOT NULL default '0',
  total_virtual_memory_size float NOT NULL default '0',
  total_visible_memory_size float NOT NULL default '0',
  windows_directory varchar(25) NOT NULL default '',
  scan_time integer NOT NULL default '0',
  original_scan_time integer NOT NULL default '0'
);
CREATE TABLE stivity.parallel_port (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(75) NOT NULL default '',
  caption varchar(50) NOT NULL default '',
  capability_descriptions varchar(75) NOT NULL default '',
  description varchar(50) NOT NULL default '',
  device_id varchar(50) NOT NULL default '',
  dma_support integer NOT NULL default '0',
  name varchar(50) NOT NULL default '',
  os_auto_discovered integer NOT NULL default '0',
  protocol_supported varchar(40) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  time_of_last_reset varchar(20) NOT NULL default ''
);
CREATE TABLE stivity.pcmcia_controller (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(100) NOT NULL default '',
  description varchar(100) NOT NULL default '',
  device_id varchar(100) NOT NULL default '',
  manufacturer varchar(50) NOT NULL default '',
  name varchar(100) NOT NULL default '',
  protocol_supported varchar(25) NOT NULL default '',
  status varchar(15) NOT NULL default ''
);
CREATE TABLE stivity.physical_memory (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  bank_label varchar(30) NOT NULL default '',
  capacity smallint NOT NULL default '0',
  caption varchar(30) NOT NULL default '',
  data_width smallint NOT NULL default '0',
  description varchar(30) NOT NULL default '',
  device_locator varchar(25) NOT NULL default '',
  form_factor varchar(25) NOT NULL default '',
  hot_swappable char(3) NOT NULL default '0',
  manufacturer varchar(25) NOT NULL default '',
  memory_type varchar(25) NOT NULL default '',
  model varchar(25) NOT NULL default '',
  name varchar(25) NOT NULL default '',
  other_identifying_info varchar(50) NOT NULL default '',
  part_number varchar(25) NOT NULL default '',
  position_in_row integer NOT NULL default '0',
  removable char(3) NOT NULL default '0',
  replaceable char(3) NOT NULL default '0',
  serial_number varchar(25) NOT NULL default '',
  sku varchar(25) NOT NULL default '',
  speed smallint NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  tag varchar(45) NOT NULL default '',
  total_width smallint NOT NULL default '0',
  type_detail varchar(25) NOT NULL default '',
  version varchar(25) NOT NULL default ''
);
CREATE TABLE stivity.physical_memory_array (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(35) NOT NULL default '',
  depth integer NOT NULL default '0',
  description varchar(25) NOT NULL default '',
  height integer NOT NULL default '0',
  hot_swappable char(3) NOT NULL default '0',
  location varchar(25) NOT NULL default '',
  manufacturer varchar(25) NOT NULL default '',
  max_capacity integer NOT NULL default '0',
  memory_devices integer NOT NULL default '0',
  memory_error_correction varchar(25) NOT NULL default '',
  model varchar(25) NOT NULL default '',
  name varchar(25) NOT NULL default '',
  other_identifying_info varchar(25) NOT NULL default '',
  part_number varchar(25) NOT NULL default '',
  serial_number varchar(25) NOT NULL default '',
  sku varchar(25) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  tag varchar(45) NOT NULL default '',
  uses varchar(35) NOT NULL default '',
  version varchar(25) NOT NULL default '',
  weight integer NOT NULL default '0',
  width integer NOT NULL default '0'
);
CREATE TABLE stivity.pnp_entity (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption text NOT NULL,
  class_guid varchar(50) NOT NULL default '',
  description varchar(125) NOT NULL default '',
  device_id varchar(125) NOT NULL default '',
  driver varchar(25) NOT NULL default '',
  manufacturer varchar(100) NOT NULL default '',
  name varchar(150) NOT NULL default '',
  service varchar(25) NOT NULL default '',
  status varchar(15) NOT NULL default ''
);
CREATE TABLE stivity.pointing_device (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(65) NOT NULL default '',
  description varchar(65) NOT NULL default '',
  device_id varchar(65) NOT NULL default '',
  device_interface varchar(25) NOT NULL default '',
  handedness varchar(25) NOT NULL default '',
  hardware_type varchar(65) NOT NULL default '',
  inf_file_name varchar(25) NOT NULL default '',
  inf_section varchar(25) NOT NULL default '',
  install_date varchar(25) NOT NULL default '',
  manufacturer varchar(25) NOT NULL default '',
  name varchar(65) NOT NULL default '',
  number_of_buttons integer NOT NULL default '0',
  pointing_type varchar(25) NOT NULL default '',
  resolution integer NOT NULL default '0',
  sample_rate integer NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  system_name varchar(75) NOT NULL default ''
);
CREATE TABLE stivity.policies (
  name text NOT NULL PRIMARY KEY,
  query text NOT NULL,
  quick_edit text NOT NULL,
  excludes text NOT NULL,
  alert text NOT NULL
);
CREATE TABLE stivity.port_connector (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(35) NOT NULL default '',
  connector_pinout varchar(25) NOT NULL default '',
  connector_type varchar(35) NOT NULL default '',
  description varchar(35) NOT NULL default '',
  external_reference_designator varchar(35) NOT NULL default '',
  internal_reference_designator varchar(35) NOT NULL default '',
  manufacturer varchar(25) NOT NULL default '',
  model varchar(35) NOT NULL default '',
  name varchar(35) NOT NULL default '',
  other_identifying_info varchar(35) NOT NULL default '',
  part_number varchar(35) NOT NULL default '',
  port_type varchar(35) NOT NULL default '',
  serial_number varchar(35) NOT NULL default '',
  sku varchar(35) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  tag varchar(35) NOT NULL default '',
  version varchar(35) NOT NULL default ''
);
CREATE TABLE stivity.portable_battery (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  battery_recharge_time integer NOT NULL default '0',
  battery_status varchar(15) NOT NULL default '',
  capacity_multiplier integer NOT NULL default '0',
  caption varchar(24) NOT NULL default '',
  chemistry varchar(25) NOT NULL default '',
  description varchar(45) NOT NULL default '',
  design_capacity float NOT NULL default '0',
  design_voltage float NOT NULL default '0',
  device_id varchar(35) NOT NULL default '',
  estimated_charge_remaining integer NOT NULL default '0',
  estimated_run_time integer NOT NULL default '0',
  expected_battery_life integer NOT NULL default '0',
  expected_life integer NOT NULL default '0',
  full_charge_capacity integer NOT NULL default '0',
  install_date varchar(25) NOT NULL default '',
  location varchar(35) NOT NULL default '',
  manufacture_date varchar(25) NOT NULL default '',
  manufacturer varchar(25) NOT NULL default '',
  max_battery_error integer NOT NULL default '0',
  max_recharge_time integer NOT NULL default '0',
  name varchar(25) NOT NULL default '',
  smart_battery_version float NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  time_on_battery integer NOT NULL default '0',
  time_to_full_charge integer NOT NULL default '0'
);
CREATE TABLE stivity.printer (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(35) NOT NULL default '',
  average_pages_per_minute integer NOT NULL default '0',
  capability_descriptions varchar(200) NOT NULL default '',
  caption varchar(100) NOT NULL default '',
  comment varchar(150) NOT NULL default '',
  is_default char(3) NOT NULL default '',
  description varchar(35) NOT NULL default '',
  detected_error_state varchar(35) NOT NULL default '',
  device_id varchar(100) NOT NULL default '',
  direct char(3) NOT NULL default '0',
  do_complete_first char(3) NOT NULL default '0',
  driver_name varchar(50) NOT NULL default '',
  enable_bidi char(3) NOT NULL default '0',
  enable_dev_query_print char(3) NOT NULL default '0',
  extended_printer_status varchar(35) NOT NULL default '',
  hidden char(3) NOT NULL default '0',
  horizontal_resolution smallint NOT NULL default '0',
  job_count_since_last_reset integer NOT NULL default '0',
  keep_printed_jobs char(3) NOT NULL default '0',
  local char(3) NOT NULL default '0',
  location varchar(65) NOT NULL default '',
  name varchar(100) NOT NULL default '',
  network char(3) NOT NULL default '0',
  port_name varchar(75) NOT NULL default '',
  printer_paper_names text NOT NULL,
  printer_state varchar(35) NOT NULL default '',
  printer_status varchar(35) NOT NULL default '',
  print_processor varchar(35) NOT NULL default '',
  published char(3) NOT NULL default '0',
  server_name varchar(50) NOT NULL default '',
  shared char(3) NOT NULL default '0',
  share_name varchar(75) NOT NULL default '',
  spool_enabled char(3) NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  system_name varchar(75) NOT NULL default '',
  time_of_last_reset varchar(25) NOT NULL default '',
  work_offline char(3) NOT NULL default '0'
);
CREATE TABLE stivity.printer_configuration (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(100) NOT NULL default '',
  "collate" char(3) NOT NULL default '0',
  color varchar(35) NOT NULL default '',
  copies smallint NOT NULL default '0',
  description varchar(100) NOT NULL default '',
  device_name varchar(100) NOT NULL default '',
  dither_type varchar(35) NOT NULL default '',
  driver_version varchar(25) NOT NULL default '',
  duplex char(3) NOT NULL default '0',
  form_name varchar(35) NOT NULL default '',
  horizontal_resolution smallint NOT NULL default '0',
  media_type varchar(35) NOT NULL default '',
  name varchar(100) NOT NULL default '',
  orientation varchar(15) NOT NULL default '',
  paper_length float NOT NULL default '0',
  paper_size float NOT NULL default '0',
  paper_width float NOT NULL default '0',
  print_quality float NOT NULL default '0',
  scale smallint NOT NULL default '0',
  setting_id varchar(75) NOT NULL default '',
  specification_version varchar(15) NOT NULL default '',
  vertical_resolution smallint NOT NULL default '0'
);
CREATE TABLE stivity.printer_driver (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(35) NOT NULL default '',
  config_file varchar(100) NOT NULL default '',
  data_file varchar(100) NOT NULL default '',
  default_data_type varchar(25) NOT NULL default '',
  dependent_files text NOT NULL,
  description varchar(35) NOT NULL default '',
  driver_path varchar(100) NOT NULL default '',
  help_file varchar(100) NOT NULL default '',
  inf_name varchar(35) NOT NULL default '',
  monitor_name varchar(75) NOT NULL default '',
  name varchar(75) NOT NULL default '',
  oem_url varchar(100) NOT NULL default '',
  started char(3) NOT NULL default '0',
  start_mode varchar(35) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  supported_platform varchar(35) NOT NULL default '',
  version varchar(35) NOT NULL default ''
);

CREATE TABLE stivity.processor (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  address_width integer  NOT NULL default '0',
  architecture varchar(35) NOT NULL default '',
  caption varchar(35) NOT NULL default '',
  cpu_status varchar(35) NOT NULL default '',
  current_clock_speed float NOT NULL default '0',
  data_width integer  NOT NULL default '0',
  description varchar(50) NOT NULL default '',
  device_id varchar(25) NOT NULL default '',
  ext_clock smallint NOT NULL default '0',
  family varchar(35) NOT NULL default '',
  l2_cache_size varchar(15) NOT NULL default '',
  l2_cache_speed smallint NOT NULL default '0',
  level integer  NOT NULL default '0',
  load_percentage integer NOT NULL default '0',
  manufacturer varchar(35) NOT NULL default '',
  max_clock_speed float NOT NULL default '0',
  name varchar(50) NOT NULL default '',
  other_family_description varchar(35) NOT NULL default '',
  processor_id varchar(35) NOT NULL default '',
  processor_type varchar(50) NOT NULL default '',
  revision smallint NOT NULL default '0',
  role varchar(15) NOT NULL default '',
  socket_designation varchar(35) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  stepping integer NOT NULL default '0',
  unique_id varchar(35) NOT NULL default '',
  upgrade_method varchar(35) NOT NULL default '',
  version varchar(50) NOT NULL default ''
);

CREATE TABLE stivity.quick_fix_engineering (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  csname varchar(75) NOT NULL default '',
  description varchar(150) NOT NULL default '',
  comments varchar(100) NOT NULL default '',
  hotfix_id varchar(35) NOT NULL default '',
  install_date varchar(25) NOT NULL default ''
);

CREATE TABLE stivity.registry (
  name text NOT NULL PRIMARY KEY,
  reg_key text NOT NULL,
  table_name varchar(75) NOT NULL default '',
  field_name varchar(100) NOT NULL default '',
  excludes text NOT NULL,
  fixed_fields text NOT NULL,
  store_type text NOT NULL
);

CREATE TABLE stivity.report (
  id integer NOT NULL PRIMARY KEY,
  name text NOT NULL,
  query text NOT NULL,
  quick_edit text NOT NULL,
  description text NOT NULL,
  creation_info varchar(150) NOT NULL default '',
  group_name varchar(100) NOT NULL default ''
);

CREATE TABLE stivity.scan_log (
  machine varchar(100) NOT NULL default '',
  epoch integer NOT NULL default '0',
  status varchar(100) NOT NULL default '',
  notes text NOT NULL,
  operating_system varchar(100) NOT NULL default '',
  type varchar(25) NOT NULL default ''
);

CREATE TABLE stivity.scsi_controller (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(35) NOT NULL default '',
  caption varchar(100) NOT NULL default '',
  controller_timeouts smallint NOT NULL default '0',
  description varchar(100) NOT NULL default '',
  device_id varchar(100) NOT NULL default '',
  device_map varchar(35) NOT NULL default '',
  driver_name varchar(50) NOT NULL default '',
  hardware_version varchar(35) NOT NULL default '',
  indexing integer NOT NULL default '0',
  manufacturer varchar(100) NOT NULL default '',
  max_data_width integer NOT NULL default '0',
  max_number_controlled smallint NOT NULL default '0',
  max_transfer_rate integer NOT NULL default '0',
  name varchar(100) NOT NULL default '',
  protocol_supported varchar(25) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  time_of_last_reset varchar(25) NOT NULL default ''
);

CREATE TABLE stivity.server_connection (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  active_time integer NOT NULL default '0',
  caption varchar(70) NOT NULL default '',
  computer_name varchar(70) NOT NULL default '',
  connection_id smallint NOT NULL default '0',
  description varchar(70) NOT NULL default '',
  name varchar(70) NOT NULL default '',
  number_of_files smallint NOT NULL default '0',
  number_of_users smallint NOT NULL default '0',
  share_name varchar(70) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  username varchar(70) NOT NULL default ''
);

CREATE TABLE stivity.service (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  accept_pause char(3) NOT NULL default '0',
  accept_stop char(3) NOT NULL default '0',
  caption varchar(150) NOT NULL default '',
  description text NOT NULL,
  display_name varchar(150) NOT NULL default '',
  name varchar(70) NOT NULL default '',
  path_name varchar(150) NOT NULL default '',
  process_id smallint NOT NULL default '0',
  service_type varchar(35) NOT NULL default '',
  started char(3) NOT NULL default '0',
  start_mode varchar(20) NOT NULL default '',
  start_name varchar(75) NOT NULL default '',
  state varchar(30) NOT NULL default '',
  status varchar(15) NOT NULL default ''
);

CREATE TABLE stivity.session (
  host varchar(15) NOT NULL default '',
  username varchar(70) NOT NULL default '',
  data text NOT NULL,
  data_type varchar(100) NOT NULL default ''
);

CREATE TABLE stivity.share (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  allow_maximum char(3) NOT NULL default '0',
  caption text NOT NULL,
  description varchar(100) NOT NULL default '',
  maximum_allowed smallint NOT NULL default '0',
  name varchar(70) NOT NULL default '',
  path varchar(150) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  type varchar(50) NOT NULL default ''
);

CREATE TABLE stivity.software (
  keyname varchar(70) default '',
  name text,
  version varchar(50) default '',
  publisher varchar(70) NOT NULL default '',
  install_date varchar(35) NOT NULL default '',
  prod_id varchar(75) NOT NULL default '',
  helplink varchar(150) NOT NULL default '',
  build_date varchar(20) NOT NULL default '',
  groupname varchar(70) NOT NULL default '',
  source_rpm varchar(150) NOT NULL default '',
  size integer NOT NULL default '0',
  summary text NOT NULL,
  PRIMARY KEY (keyname, name, version)
);

CREATE TABLE stivity.software_meter (
  title text NOT NULL,
  version varchar(50) NOT NULL default '',
  licenses integer NOT NULL default '0',
  notes varchar(100) NOT NULL default '',
  alert varchar(75) NOT NULL default ''
);

CREATE TABLE stivity.sound_device (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(35) NOT NULL default '',
  caption varchar(100) NOT NULL default '',
  description varchar(100) NOT NULL default '',
  device_id varchar(100) NOT NULL default '',
  dma_buffer_size smallint NOT NULL default '0',
  driver varchar(15) NOT NULL default '',
  manufacturer varchar(50) NOT NULL default '',
  mpu_401_address varchar(35) NOT NULL default '',
  name varchar(100) NOT NULL default '',
  pnp_device_id varchar(100) NOT NULL default '',
  product_name varchar(100) NOT NULL default '',
  status varchar(15) NOT NULL default ''
);

CREATE TABLE stivity.system_enclosure (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  audible_alarm char(3) NOT NULL default '0',
  breach_description varchar(70) NOT NULL default '',
  cable_management_strategy varchar(70) NOT NULL default '',
  caption varchar(70) NOT NULL default '',
  chassis_type varchar(70) NOT NULL default '',
  current_required_or_produced smallint NOT NULL default '0',
  depth integer NOT NULL default '0',
  description varchar(70) NOT NULL default '',
  heat_generation smallint NOT NULL default '0',
  height integer NOT NULL default '0',
  lock_present char(3) NOT NULL default '0',
  manufacturer varchar(70) NOT NULL default '',
  model varchar(70) NOT NULL default '',
  name varchar(35) NOT NULL default '',
  number_of_power_cords integer NOT NULL default '0',
  other_identifying_info varchar(100) NOT NULL default '',
  part_number varchar(70) NOT NULL default '',
  powered_on char(3) NOT NULL default '0',
  removable char(3) NOT NULL default '0',
  replaceable char(3) NOT NULL default '0',
  security_breach varchar(15) NOT NULL default '',
  security_status varchar(45) NOT NULL default '',
  serial_number varchar(75) NOT NULL default '',
  service_philosophy varchar(75) NOT NULL default '',
  sku varchar(45) NOT NULL default '',
  smbios_asset_tag varchar(75) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  tag varchar(45) NOT NULL default '',
  version varchar(45) NOT NULL default '',
  visible_alarm char(3) NOT NULL default '0',
  weight smallint NOT NULL default '0',
  width integer NOT NULL default '0'
);

CREATE TABLE stivity.system_slot (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(25) NOT NULL default '',
  connector_pinout varchar(70) NOT NULL default '',
  connector_type varchar(35) NOT NULL default '',
  current_usage varchar(25) NOT NULL default '',
  description varchar(25) NOT NULL default '',
  height_allowed integer NOT NULL default '0',
  length_allowed integer NOT NULL default '0',
  manufacturer varchar(70) NOT NULL default '',
  max_data_width integer NOT NULL default '0',
  model varchar(70) NOT NULL default '',
  name varchar(25) NOT NULL default '',
  number integer NOT NULL default '0',
  other_identifying_info varchar(70) NOT NULL default '',
  part_number varchar(70) NOT NULL default '',
  pme_signal char(3) NOT NULL default '0',
  powered_on char(3) NOT NULL default '0',
  purpose_description varchar(70) NOT NULL default '',
  serial_number varchar(70) NOT NULL default '',
  shared char(3) NOT NULL default '0',
  sku varchar(70) NOT NULL default '',
  slot_designation varchar(25) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  supports_hot_plug char(3) NOT NULL default '0',
  tag varchar(35) NOT NULL default '',
  thermal_rating integer NOT NULL default '0',
  vcc_mixed_voltage_support varchar(25) NOT NULL default '',
  version varchar(25) NOT NULL default '',
  vpp_mixed_voltage_support integer NOT NULL default '0'
);

CREATE TABLE stivity.tapedrive (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  availability varchar(35) NOT NULL default '',
  caption varchar(70) NOT NULL default '',
  compression varchar(35) NOT NULL default '',
  compression_method varchar(70) NOT NULL default '',
  description varchar(70) NOT NULL default '',
  device_id varchar(70) NOT NULL default '',
  ecc char(3) NOT NULL default '0',
  id varchar(70) NOT NULL default '',
  manufacturer varchar(50) NOT NULL default '',
  max_media_size integer NOT NULL default '0',
  media_type varchar(25) NOT NULL default '',
  name varchar(70) NOT NULL default '',
  needs_cleaning char(3) NOT NULL default '0',
  number_of_media_supported integer NOT NULL default '0',
  status varchar(15) NOT NULL default ''
);

CREATE TABLE stivity.temperature_probe (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  accuracy smallint NOT NULL default '0',
  availability varchar(35) NOT NULL default '',
  caption varchar(35) NOT NULL default '',
  current_reading smallint NOT NULL default '0',
  description varchar(70) NOT NULL default '',
  device_id varchar(50) NOT NULL default '',
  is_linear integer NOT NULL default '0',
  name varchar(70) NOT NULL default '',
  nominal_reading smallint NOT NULL default '0',
  normal_max smallint NOT NULL default '0',
  normal_min smallint NOT NULL default '0',
  resolution smallint NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  tolerance smallint NOT NULL default '0'
);

CREATE TABLE stivity.timezone (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  caption varchar(70) NOT NULL default '',
  description varchar(70) NOT NULL default ''
);

CREATE TABLE stivity.user_account (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  name varchar(75) NOT NULL default '',
  account_type varchar(100) NOT NULL default '',
  caption varchar(75) NOT NULL default '',
  description varchar(150) NOT NULL default '',
  disabled char(3) NOT NULL default '0',
  domain varchar(70) NOT NULL default '',
  full_name varchar(100) NOT NULL default '',
  gid integer NOT NULL default '0',
  home_dir varchar(70) NOT NULL default '',
  local_account char(3) NOT NULL default '0',
  lockout char(3) NOT NULL default '0',
  password_changeable char(3) NOT NULL default '0',
  password_expires char(3) NOT NULL default '0',
  password_required char(3) NOT NULL default '0',
  shell varchar(40) NOT NULL default '',
  sid varchar(70) NOT NULL default '',
  sid_type varchar(25) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  uid integer NOT NULL default '0'
);

CREATE TABLE stivity.video_controller (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  adapter_compatibility varchar(40) NOT NULL default '',
  adapter_dac_type varchar(45) NOT NULL default '',
  adapter_ram integer NOT NULL default '0',
  availability varchar(35) NOT NULL default '',
  caption varchar(70) NOT NULL default '',
  current_bits_per_pixel integer NOT NULL default '0',
  current_horizontal_resolution smallint NOT NULL default '0',
  current_number_of_colors integer NOT NULL default '0',
  current_refresh_rate integer NOT NULL default '0',
  current_scan_mode varchar(20) NOT NULL default '',
  current_vertical_resolution smallint NOT NULL default '0',
  description varchar(70) NOT NULL default '',
  device_id varchar(35) NOT NULL default '',
  driver_date varchar(35) NOT NULL default '',
  driver_version varchar(50) NOT NULL default '',
  inf_filename varchar(35) NOT NULL default '',
  inf_section varchar(50) NOT NULL default '',
  installed_display_drivers varchar(40) NOT NULL default '',
  max_memory_supported integer NOT NULL default '0',
  max_refresh_rate integer NOT NULL default '0',
  min_refresh_rate integer NOT NULL default '0',
  monochrome char(3) NOT NULL default '0',
  name varchar(70) NOT NULL default '',
  protocol_supported varchar(15) NOT NULL default '',
  specification_version varchar(15) NOT NULL default '',
  status varchar(15) NOT NULL default '',
  video_architecture varchar(25) NOT NULL default '',
  video_memory_type varchar(25) NOT NULL default '',
  video_mode varchar(25) NOT NULL default '',
  video_mode_description varchar(50) NOT NULL default '',
  video_processor varchar(70) NOT NULL default ''
);

CREATE TABLE stivity.voltage_probe (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  accuracy smallint NOT NULL default '0',
  availability varchar(25) NOT NULL default '',
  caption varchar(70) NOT NULL default '',
  current_reading smallint NOT NULL default '0',
  description varchar(70) NOT NULL default '',
  device_id varchar(70) NOT NULL default '',
  is_linear integer NOT NULL default '0',
  name varchar(70) NOT NULL default '',
  nominal_reading smallint NOT NULL default '0',
  normal_max smallint NOT NULL default '0',
  normal_min smallint NOT NULL default '0',
  resolution smallint NOT NULL default '0',
  status varchar(15) NOT NULL default '',
  tolerance smallint NOT NULL default '0'
);

CREATE TABLE stivity.windows_product_activation (
  keyname varchar(70) NOT NULL default '' PRIMARY KEY,
  activation_required char(3) NOT NULL default '0',
  caption varchar(70) NOT NULL default '',
  description varchar(70) NOT NULL default '',
  is_notification_on char(3) NOT NULL default '0',
  product_id varchar(45) NOT NULL default '',
  remaining_evaluation_period integer NOT NULL default '0',
  remaining_grace_period integer NOT NULL default '0',
  server_name varchar(75) NOT NULL default ''
);

CREATE TABLE perfrpt.adisctemplates (
  adisctemplates_id integer PRIMARY KEY,
  vendor_id integer,
  rpttemplates_id integer,
  device_id integer
);

CREATE TABLE perfrpt.customrpts (
  report_id integer PRIMARY KEY,
  user_id integer,
  type smallint,
  name varchar(100),
  descr text,
  parameters text,
  added_by varchar(50),
  timestamp integer
);

CREATE TABLE perfrpt.deviceindex (
  deviceindex_id integer PRIMARY KEY,
  sysobjectid varchar(100),
  vendor_id integer,
  label varchar(100),
  default_id integer,
  dype varchar(100)
);

CREATE TABLE perfrpt.favorites (
  favorite_id integer PRIMARY KEY,
  user_id integer,
  parameters text
);

CREATE TABLE perfrpt.filters (
  filter_id integer PRIMARY KEY,
  name varchar(100),
  type smallint
);

CREATE TABLE perfrpt.groupindex (
  groupindex_id integer PRIMARY KEY,
  name varchar(100)
);

CREATE TABLE perfrpt.groupmembers (
  member_id integer PRIMARY KEY,
  reportindex_id integer,
  groupindex_id integer,
  groupOrder smallint
);

CREATE TABLE perfrpt.history (
  history_id integer PRIMARY KEY,
  report_id integer,
  inVal  bigint,
  outVal bigint,
  timestamp integer
);

CREATE TABLE perfrpt.reportindex (
  reportindex_id integer PRIMARY KEY,
  name varchar(200),
  rpttemplates_id integer,
  rptParameters text,
  ipaddress inet,
  community varchar(50),
  status smallint,
  nMethod varchar(30),
  nParameters text,
  dtype varchar(100),
  rtype varchar(100),
  timestamp integer,
  added_by varchar(50)
);

CREATE TABLE perfrpt.rpttemplates (
  rpttemplates_id integer PRIMARY KEY,
  name varchar(100),
  descr text,
  type varchar(20),
  testString varchar(200),
  ifDescr varchar(100),
  ifIndex varchar(100),
  parameters text,
  vendor_id integer,
  rtype varchar(100)
);

CREATE TABLE perfrpt.typecatalog (
  type_id integer PRIMARY KEY,
  name varchar(50),
  type varchar(50),
  added_by integer,
  timestamp integer
);

CREATE TABLE perfrpt.vendorindex (
  verndorindex_id integer PRIMARY KEY,
  sysobejctid varchar(100),
  name varchar(50)
);

CREATE TABLE security.users (
	user_id integer PRIMARY KEY,
	password varchar(40),
	user_name varchar(50),
	first_name varchar(50),
	last_name varchar(50)
);

CREATE TABLE security.user_comp (
  comp_id integer PRIMARY KEY,
  user_id integer,
  salary text,
  rate text,
  method boolean
);

CREATE TABLE webnm.cameras (
  camera_id integer PRIMARY KEY,
  name varchar(64),
  type integer,
  Ip inet,
  url varchar(128),
  internal_notes text,
  width integer,
  height integer,
  html text
);

CREATE TABLE webnm.invmgr_community (
  community_id integer PRIMARY KEY,
  community varchar(50) NOT NULL,
  timestamp integer NOT NULL,
  added_by integer NOT NULL
);

CREATE TABLE webnm.invmgr_inventory (
  inv_id integer PRIMARY KEY,
  dns varchar(100),
  name varchar(100),
  netbios varchar(100),
  ipAddress inet,
  communinity varchar(50),
  device_id integer,
  device_name varchar(100),
  vendor_id integer,
  vendor_name varchar(100),
  c1 text,
  c2 text,
  c3 text,
  c4 text,
  c5 text,
  c6 text,
  c7 text,
  c8 text,
  c9 text,
  c10 text,
  notes text,
  status smallint,
  bridge smallint,
  timestamp integer,
  added_by integer
);

CREATE TABLE webnm.invmgr_mac (
  mac_id integer PRIMARY KEY,
  inv_id integer,
  port integer,
  macaddr varchar(100),
  timestamp integer 
);

CREATE TABLE webnm.invmgr_policy (
  policy_id integer PRIMARY KEY,
  policy_match varchar(12),
  c1n varchar(100), 
  c1s text,
  c1v smallint,
  c2n varchar(100), 
  c2s text,
  c2v smallint,
  c3n varchar(100), 
  c3s text,
  c3v smallint,
  c4n varchar(100), 
  c4s text,
  c4v smallint,
  c5n varchar(100), 
  c5s text,
  c5v smallint,
  c6n varchar(100), 
  c6s text,
  c6v smallint,
  c7n varchar(100), 
  c7s text,
  c7v smallint,
  c8n varchar(100), 
  c8s text,
  c8v smallint,
  c9n varchar(100), 
  c9s text,
  c9v smallint,
  c10n varchar(100), 
  c10s text,
  c10v smallint,
  timestamp integer,
  added_by integer
);

CREATE TABLE webnm.overview (
  overview_id integer PRIMARY KEY,
  name varchar(200),
  category varchar(200),
  url text,
  query text,
  setCode  text
);

CREATE TABLE webnm.remote_agent (
  agent_id integer PRIMARY KEY,
  name varchar(50),
  tcp  integer,
  notes text,
  added_by varchar(50),
  timestamp integer
);

CREATE TABLE webnm.remote_profiles (
  remote_id integer PRIMARY KEY,
  agent_id integer,
  name varchar(50),
  tcp integer,
  notes text,
  added_by varchar(50), 
  timestamp integer
);

CREATE TABLE webnm.supported_cameras (
  type integer PRIMARY KEY,
  vendor varchar(64),
  model varchar(64),
  width smallint, 
  height smallint,
  html text
);

CREATE TABLE webnm.wizard_tracker(  
  wizard_id integer PRIMARY KEY,
  name varchar(100),
  descr text,
  url text,
  frame varchar(50),
  query text,
  secCode varchar(50),
  status_id smallint,
  updated_by varchar(50),
  timestamp integer
);

CREATE TABLE webtts.attachments (
  attachment_id integer PRIMARY KEY,
  filename text,
  reference_id  integer,
  resource_type  integer,
  hitcount integer,
  added_by varchar(50),
  timestamp int
);

CREATE TABLE webtts.calendar (
  calendar_id integer PRIMARY KEY,
  user_id integer,
  eventName text,
  evemtDescr text,
  "start" integer,
  "end" integer,
  parent_id integer,
  added_by varchar(50),
  timestamp integer
);

CREATE TABLE webtts.enterprise (
  parent_id text,
  company_id text 
);

CREATE TABLE webtts.faq (
  faq_id integer PRIMARY KEY,
  question text,
  answer text,
  public boolean,
  hitcount integer,
  kb1 text,
  kb2 text,
  kb3 text,
  kb4 text,
  kb5 text,
  added_by varchar(50),
  timestamp integer
);

CREATE TABLE webtts.glossary( 
  glossary_id integer PRIMARY KEY,
  term text,
  definition text,
  public boolean,
  hitcount integer,
  added_by varchar(50),
  timestamp integer
);

CREATE TABLE webtts.hitcount (
  count_id integer PRIMARY KEY,
  user_id text,
  reference_id integer,
  resource_type integer,
  timestamp integer 
);

CREATE TABLE webtts.import_profiles (
  id integer PRIMARY KEY,
  count integer,
  db_ip inet,
  db_username text,
  db_passwort text,
  db_name text,
  db_type text,
  dbdesc text,
  co1 text,
  co2 text,
  co3 text,
  co4 text,
  co5 text,
  co6 text,
  co7 text,
  co8 text,
  co9 text,
  co10 text,
  co11 text,
  co12 text,
  co13 text,
  co14 text,
  co15 text,
  co16 text,
  co17 text,
  co18 text,
  cu1 text,
  cu2 text,
  cu3 text,
  cu4 text,
  cu5 text,
  cu6 text,
  cu7 text,
  cu8 text,
  cu9 text,
  cu10 text,
  cu11 text,
  cu12 text,
  cu13 text,
  cu14 text,
  cu15 text,
  cu16 text
);

CREATE TABLE webtts.policies ( 
  policy_id integer PRIMARY KEY,
  type text,  
  parameters text,
  added_by text,
  timestamp integer
);

CREATE TABLE webtts.profiles (
  profile_id integer PRIMARY KEY,
  profile_mame text,
  c1v text,
  c1t text,
  c1n text,
  c2v text,
  c2t text,
  c2n text,
  c3v text,
  c3t text,
  c3n text,
  c4v text,
  c4t text,
  c4n text,
  c5v text,
  c5t text,
  c5n text,
  c6v text,
  c6t text,
  c6n text,
  c7v text,
  c7t text,
  c7n text,
  c8v text,
  c8t text,
  c8n text,
  c9v text,
  c9t text,
  c9n text,
  c10v text,
  c10t text,
  c10n text,
  c11v text,
  c11t text,
  c11n text,
  c12v text,
  c12t text,
  c12n text,
  c13v text,
  c13t text,
  c13n text,
  c14v text,
  c14t text,
  c14n text,
  c15v text,
  c15t text,
  c15n text,
  c16v text,
  c16t text,
  c16n text,
  c17v text,
  c17t text,
  c17n text,
  c18v text,
  c18t text,
  c18n text,
  c19v text,
  c19t text,
  c19n text,
  c20v text,
  c20t text,
  c20n text,
  kb1n text,
  kb1v text,
  kb1t text,
  kb2n text,
  kb2v text,
  kb2t text,
  kb3n text,
  kb3v text,
  kb3t text,
  kb4n text,
  kb4v text,
  kb4t text,
  kb5n text,
  kb5v text,
  kb5t text,
  co1n text,
  co1v text,
  co1t text,
  co2n text,
  co2v text,
  co2t text,
  co3n text,
  co3v text,
  co3t text,
  co4n text,
  co4v text,
  co4t text,
  co5n text,
  co5v text,
  co5t text,
  cu1n text,
  cu1v text,
  cu1t text,
  cu2n text,
  cu2v text,
  cu2t text,
  cu3n text,
  cu3v text,
  cu3t text,
  cu4n text,
  cu4v text,
  cu4t text,
  cu5n text,
  cu5v text,
  cu5t text
);

CREATE TABLE webtts.qmembers (
  queue_id integer,
  user_id integer,
  PRIMARY KEY (queue_id,user_id)
);

CREATE TABLE webtts.queues (  
  queue_id integer PRIMARY KEY,
  queue_name text
);

CREATE TABLE webtts.reports (
  report_id integer PRIMARY KEY,
  user_id integer,
  name text,
  "desc" text,
  parameters text,
  added_by varchar(50),
  timestamp integer
);

CREATE TABLE webtts.surveys (
  survey_id integer PRIMARY KEY,
  reference_id integer,
  resource_type integer,
  vote text,
  comments text,
  added_by varchar(50),
  timestamp integer
);

CREATE TABLE webtts.tasks (
  task_id integer PRIMARY KEY,
  user_id integer,
  taskName text,
  taskDescr text,
  dueDate integer,
  reminder integer,
  added_by varchar(50),
  timestamp integer
);

CREATE TABLE webtts.tickets (
  ticket_number integer PRIMARY KEY,
  added_by varchar(50),
  company_id text,
  customer_id text,
  assigned_to text,
  open_time integer,
  last_update integer,
  total_time integer,
  description text,
  internal_notes text,
  C1 text,
  C2 text,
  C3 text,
  C4 text,
  C5 text,
  C6 text,
  C7 text,
  C8 text,
  C9 text,
  C10 text,
  C11 text,
  C12 text,
  C13 text,
  C14 text,
  C15 text,
  C16 text,
  C17 text,
  C18 text,
  C19 text,
  C20 text
);

CREATE TABLE webtts.usergroups (
  group_id integer PRIMARY KEY,
  groupName text,
  members text,
  user_id integer,
  added_by varchar(50),
  timestamp integer
);

-- foreign key constraints

--crm.call_history 
ALTER TABLE crm.call_history ADD FOREIGN KEY (reference_id) REFERENCES webtts.tickets;
ALTER TABLE crm.call_history ADD FOREIGN KEY (company_id) REFERENCES crm.organizations;
ALTER TABLE crm.call_history ADD FOREIGN KEY (customer_id) REFERENCES crm.individuals;
-- crm.individuals
ALTER TABLE crm.individuals ADD FOREIGN KEY (company_id) REFERENCES crm.organizations;
-- logalot.bulletin_board
ALTER TABLE logalot.bulletin_board ADD FOREIGN KEY (policy_id) REFERENCES logalot.policies;
-- logalot.bulletin_board_header
ALTER TABLE logalot.bulletin_board_header ADD FOREIGN KEY (policy_id) REFERENCES logalot.policies;
-- logalot.eventlog_history
ALTER TABLE logalot.eventlog_history ADD FOREIGN KEY (policy_id) REFERENCES logalot.policies;
-- logalot.history
ALTER TABLE logalot.history ADD FOREIGN KEY (policy_id) REFERENCES logalot.policies;
-- perfrpt.adisktemplates
ALTER TABLE perfrpt.adisctemplates ADD FOREIGN KEY (device_id) REFERENCES perfrpt.deviceindex;
ALTER TABLE perfrpt.adisctemplates ADD FOREIGN KEY (vendor_id) REFERENCES perfrpt.vendorindex;
ALTER TABLE perfrpt.adisctemplates ADD FOREIGN KEY (rpttemplates_id) REFERENCES perfrpt.rpttemplates;
-- perfrpt.customrpts
ALTER TABLE perfrpt.customrpts ADD FOREIGN KEY (user_id) REFERENCES security.users;
-- perfrpt.deviceindex
ALTER TABLE perfrpt.deviceindex ADD FOREIGN KEY (vendor_id) REFERENCES perfrpt.vendorindex;
ALTER TABLE perfrpt.deviceindex ADD FOREIGN KEY (default_id) REFERENCES perfrpt.vendorindex;
-- perfrpt.favorites
ALTER TABLE perfrpt.favorites ADD FOREIGN KEY (user_id) REFERENCES security.users;
-- perfrpt.groupmembers
ALTER TABLE perfrpt.groupmembers ADD FOREIGN KEY (reportindex_id) REFERENCES perfrpt.reportindex;
ALTER TABLE perfrpt.groupmembers ADD FOREIGN KEY (groupindex_id) REFERENCES perfrpt.groupindex;
-- perfrpt.history
ALTER TABLE perfrpt.history ADD FOREIGN KEY (report_id) REFERENCES perfrpt.reportindex;
-- perfrpt.reportindex
ALTER TABLE perfrpt.reportindex ADD FOREIGN KEY (rpttemplates_id) REFERENCES perfrpt.rpttemplates;
-- perfrpt.rpttemplates
ALTER TABLE perfrpt.rpttemplates ADD FOREIGN KEY (vendor_id) REFERENCES perfrpt.vendorindex;
-- perfrpt.typecatalog
ALTER TABLE perfrpt.typecatalog ADD FOREIGN KEY (added_by) REFERENCES security.users;
-- security.user_comp
ALTER TABLE security.user_comp ADD FOREIGN KEY (user_id) REFERENCES security.users;
-- webnm.invmgr_community 
ALTER TABLE webnm.invmgr_community ADD FOREIGN KEY (added_by) REFERENCES security.users;
-- webnm.invmgr_inventory
ALTER TABLE webnm.invmgr_inventory ADD FOREIGN KEY (device_id) REFERENCES perfrpt.deviceindex;
ALTER TABLE webnm.invmgr_inventory ADD FOREIGN KEY (vendor_id) REFERENCES perfrpt.vendorindex;
ALTER TABLE webnm.invmgr_inventory ADD FOREIGN KEY (added_by) REFERENCES security.users;
-- webnm.invmgr_mac 
ALTER TABLE webnm.invmgr_mac ADD FOREIGN KEY (inv_id) REFERENCES webnm.invmgr_inventory;
-- webnm.invmgr_policy
ALTER TABLE webnm.invmgr_policy ADD FOREIGN KEY (added_by) REFERENCES security.users;
-- webnm.remote_profiles
ALTER TABLE webnm.remote_profiles ADD FOREIGN KEY (agent_id) REFERENCES webnm.remote_agent;
-- webtts.attachments
ALTER TABLE webtts.attachments ADD FOREIGN KEY (reference_id) REFERENCES webtts.tickets;
-- webtts.calendar
ALTER TABLE webtts.calendar ADD FOREIGN KEY (user_id) REFERENCES security.users;
-- webtts.enterprise
ALTER TABLE webtts.enterprise ADD FOREIGN KEY (parent_id) REFERENCES crm.organizations;
ALTER TABLE webtts.enterprise ADD FOREIGN KEY (company_id) REFERENCES crm.organizations;
-- webtts.hitcount
ALTER TABLE webtts.hitcount ADD FOREIGN KEY (reference_id) REFERENCES webtts.tickets;
-- webtts.qmembers
ALTER TABLE webtts.qmembers ADD FOREIGN KEY (queue_id) REFERENCES webtts.queues;
ALTER TABLE webtts.qmembers ADD FOREIGN KEY (user_id) REFERENCES security.users;
-- webtts.reports
ALTER TABLE webtts.reports ADD FOREIGN KEY (user_id) REFERENCES security.users;
-- webtts.surveys
ALTER TABLE webtts.surveys ADD FOREIGN KEY (reference_id) REFERENCES webtts.tickets;
-- webtts.tasks
ALTER TABLE webtts.tasks ADD FOREIGN KEY (user_id) REFERENCES security.users;
-- webtts.usergroups
ALTER TABLE webtts.usergroups ADD FOREIGN KEY (user_id) REFERENCES security.users;

