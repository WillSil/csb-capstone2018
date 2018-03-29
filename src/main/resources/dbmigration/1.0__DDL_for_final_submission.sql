-- apply changes
create table administrator_email_sync_business_clients (
  business_client_sync_id       integer not null,
  sync_id                       integer,
  client_id                     bigint,
  business_id                   bigint,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_administrator_email_sync_business_clients primary key (business_client_sync_id)
);
create sequence administrator_email_sync_business_clients_seq;

create table auth_user (
  user_id                       varchar(255) not null,
  refresh_token                 varchar(255),
  access_token                  varchar(255),
  expiration_time_unix_millis   bigint not null,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint uq_auth_user_user_id unique (user_id),
  constraint pk_auth_user primary key (user_id)
);

create table client_business (
  business_id                   bigserial not null,
  business_name                 varchar(255) not null,
  business_email                varchar(255),
  business_phone_number         varchar(255),
  business_address              varchar(255),
  business_industry             varchar(255),
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_client_business primary key (business_id)
);

create table business_status_child_model (
  status_child_type             varchar(255) not null,
  status_parent_type            varchar(255),
  status_child_description      varchar(255),
  child_order_number            integer not null,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_business_status_child_model primary key (status_child_type)
);

create table client (
  client_id                     bigserial not null,
  business_id                   bigint not null,
  client_name                   varchar(255),
  client_email                  varchar(255),
  client_phone                  varchar(255),
  client_notes                  varchar(255),
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_client primary key (client_id)
);

create table client_business_files (
  file_id                       varchar(255) not null,
  client_id                     bigint not null,
  business_id                   integer not null,
  user_role_type                varchar(255) not null,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint uq_client_business_files_file_id unique (file_id),
  constraint pk_client_business_files primary key (file_id)
);

create table administrator_email_sync (
  sync_id                       integer not null,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_administrator_email_sync primary key (sync_id)
);
create sequence administrator_email_sync_seq;

create table engages (
  engagement_id                 serial not null,
  engagement_name               varchar(255),
  is_active                     boolean default false not null,
  business_id                   bigint,
  status_child_type             varchar(255),
  engagement_notes              TEXT,
  date_started                  timestamptz not null,
  date_ended                    timestamptz,
  date_implemented              timestamptz,
  date_status_last_updated      timestamptz,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_engages primary key (engagement_id)
);

create table engages_file (
  file_id                       varchar(255) not null,
  engagement_id                 bigint not null,
  user_role_type                varchar(255),
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint uq_engages_file_file_id unique (file_id),
  constraint pk_engages_file primary key (file_id)
);

create table invited_user (
  email                         varchar(255) not null,
  user_role_type                varchar(255),
  invite_date                   timestamptz,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_invited_user primary key (email)
);

create table business_status_parent_model (
  status_parent_type            varchar(255) not null,
  status_parent_description     varchar(255),
  phase_number                  integer not null,
  parent_order_number           integer not null,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_business_status_parent_model primary key (status_parent_type)
);

create table uploaded_file (
  file_id                       varchar(255) not null,
  file_name                     varchar(255) not null,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_uploaded_file primary key (file_id)
);

create table _user (
  user_id                       varchar(255) not null,
  email                         varchar(255) not null,
  name                          varchar(255) not null,
  google_account_id             varchar(255) not null,
  jwt                           varchar(255),
  a_dummy_field                 varchar(255),
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint uq__user_email unique (email),
  constraint uq__user_google_account_id unique (google_account_id),
  constraint pk__user primary key (user_id)
);

create table user_notification (
  notification_id               varchar(255) not null,
  user_id                       varchar(255),
  notification_message          varchar(255),
  notification_date             timestamptz,
  business_id                   bigint,
  engagement_id                 integer,
  is_seen                       boolean default false not null,
  notification_type             integer,
  business_client_sync_id       integer,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_user_notification primary key (notification_id)
);

create table user_notification_type (
  notification_type             serial not null,
  notification_description      varchar(255),
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_user_notification_type primary key (notification_type)
);

create table user_role (
  user_id                       varchar(255) not null,
  user_role_type                varchar(255) not null,
  date_added                    timestamptz,
  date_ended                    timestamptz,
  is_active                     boolean default false not null,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint uq_user_role_user_id unique (user_id),
  constraint pk_user_role primary key (user_id)
);

create table user_role_model (
  user_role_type                varchar(255) not null,
  user_role_description         varchar(255),
  user_role_priority            integer not null,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_user_role_model primary key (user_role_type)
);

create table user_settings (
  settings_type                 varchar(255) not null,
  user_id                       varchar(255),
  settings_numeric_value        float,
  settings_string_value         varchar(255),
  settings_date_value           timestamptz,
  date_created                  TIMESTAMP default current_timestamp not null,
  date_last_updated             TIMESTAMP default current_timestamp not null,
  constraint pk_user_settings primary key (settings_type)
);

alter table administrator_email_sync_business_clients add constraint fk_administrator_email_sync_business_clients_sync_id foreign key (sync_id) references administrator_email_sync (sync_id) on delete restrict on update restrict;
create index ix_administrator_email_sync_business_clients_sync_id on administrator_email_sync_business_clients (sync_id);

alter table administrator_email_sync_business_clients add constraint fk_administrator_email_sync_business_clients_client_id foreign key (client_id) references client (client_id) on delete restrict on update restrict;
create index ix_administrator_email_sync_business_clients_client_id on administrator_email_sync_business_clients (client_id);

alter table administrator_email_sync_business_clients add constraint fk_administrator_email_sync_business_clients_business_id foreign key (business_id) references client_business (business_id) on delete restrict on update restrict;
create index ix_administrator_email_sync_business_clients_business_id on administrator_email_sync_business_clients (business_id);

alter table auth_user add constraint fk_auth_user_user_id foreign key (user_id) references _user (user_id) on delete restrict on update restrict;

alter table client_business_files add constraint fk_client_business_files_file_id foreign key (file_id) references uploaded_file (file_id) on delete restrict on update restrict;

alter table client_business_files add constraint fk_client_business_files_user_role_type foreign key (user_role_type) references user_role_model (user_role_type) on delete restrict on update restrict;
create index ix_client_business_files_user_role_type on client_business_files (user_role_type);

alter table engages add constraint fk_engages_business_id foreign key (business_id) references client_business (business_id) on delete restrict on update restrict;
create index ix_engages_business_id on engages (business_id);

alter table engages add constraint fk_engages_status_child_type foreign key (status_child_type) references business_status_child_model (status_child_type) on delete restrict on update restrict;
create index ix_engages_status_child_type on engages (status_child_type);

alter table engages_file add constraint fk_engages_file_file_id foreign key (file_id) references uploaded_file (file_id) on delete restrict on update restrict;

alter table engages_file add constraint fk_engages_file_user_role_type foreign key (user_role_type) references user_role_model (user_role_type) on delete restrict on update restrict;
create index ix_engages_file_user_role_type on engages_file (user_role_type);

alter table invited_user add constraint fk_invited_user_user_role_type foreign key (user_role_type) references user_role_model (user_role_type) on delete restrict on update restrict;
create index ix_invited_user_user_role_type on invited_user (user_role_type);

alter table user_notification add constraint fk_user_notification_business_id foreign key (business_id) references client_business (business_id) on delete restrict on update restrict;
create index ix_user_notification_business_id on user_notification (business_id);

alter table user_notification add constraint fk_user_notification_engagement_id foreign key (engagement_id) references engages (engagement_id) on delete restrict on update restrict;
create index ix_user_notification_engagement_id on user_notification (engagement_id);

alter table user_notification add constraint fk_user_notification_notification_type foreign key (notification_type) references user_notification_type (notification_type) on delete restrict on update restrict;
create index ix_user_notification_notification_type on user_notification (notification_type);

alter table user_notification add constraint fk_user_notification_business_client_sync_id foreign key (business_client_sync_id) references administrator_email_sync_business_clients (business_client_sync_id) on delete restrict on update restrict;
create index ix_user_notification_business_client_sync_id on user_notification (business_client_sync_id);

alter table user_role add constraint fk_user_role_user_id foreign key (user_id) references _user (user_id) on delete restrict on update restrict;

alter table user_role add constraint fk_user_role_user_role_type foreign key (user_role_type) references user_role_model (user_role_type) on delete restrict on update restrict;
create index ix_user_role_user_role_type on user_role (user_role_type);

