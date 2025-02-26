create table if not exists attachments
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	file_path varchar(200) null,
	file_size bigint null,
	object_id bigint null
);
create table if not exists baselines
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	collection_id bigint null,
	document_history_ids json null,
	project_id bigint null,
	type varchar(200) null,
	tracker_item_history_ids json null
);


create table if not exists change_logs
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	message varchar(200) null,
	message_type varchar(200) null,
	new_value json null,
	object_id bigint null,
	old_value json null,
	target json null
);

create table if not exists code_repositories
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	access_token varchar(200) null,
	application_id varchar(200) null,
	code varchar(200) null,
	created_at bigint null,
	expires_in int null,
	grant_user_name varchar(200) null,
	host varchar(200) null,
	redirect_uri varchar(200) null,
	refresh_token varchar(200) null,
	secret varchar(200) null,
	token_type varchar(200) null,
	type varchar(200) null,
	web_hook_token varchar(200) null,
	web_hook_url varchar(200) null
);

create table if not exists collections
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	documents json null,
	project_id bigint null
);

create table if not exists comments
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	mark_deleted tinyint(1) default 0 null,
	message varchar(200) null,
	object_id bigint null,
	replies json null,
	reply_to_id bigint null
);

create table if not exists deliverables
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	attachment_id bigint null,
	committed tinyint(1) default 0 not null,
	milestone_id bigint null,
	ordinary int null,
	project_id bigint null,
	type varchar(200) null,
	url varchar(200) null
);

create table if not exists document_history
(
	id bigint not null
		primary key,
	object_id bigint not null,
	name varchar(200) null,
	description longtext null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	blocks json null,
	page_id bigint null,
	project_id bigint null,
	revision bigint null
);

create table if not exists document_tracker_items
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	document_id bigint null,
	item_id bigint null
);

create table if not exists documents
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	blocks json null,
	page_id bigint null,
	version bigint null,
	elements json null
);

create table if not exists enum_categories
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	code varchar(200) null,
	dynamic tinyint(1) default 0 not null,
	project_id bigint null,
	script varchar(200) null,
	`system` tinyint(1) default 0 not null
);

create table if not exists enum_items
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	background_color varchar(200) null,
	category_id bigint null,
	code varchar(200) null,
	color varchar(200) null,
	ordinary int null,
	project_id bigint null,
	`system` tinyint(1) default 0 not null
);

create table if not exists hyperlinks
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	link_path varchar(200) null,
	link_role varchar(200) null,
	link_type varchar(200) null,
	object_id bigint null
);

create table if not exists logs
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	module varchar(200) null,
	remote_address varchar(200) null
);

create table if not exists notifications
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	href varchar(200) null,
	`read` tinyint(1) default 0 not null,
	read_date json null,
	receiver_id bigint null,
	title varchar(200) null
);

create table if not exists organizations
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	child_count json null,
	hierarchy_code varchar(200) null,
	parent_id bigint null
);

create table if not exists params
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	code varchar(200) null,
	content varchar(200) null
);

create table if not exists permissions
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	identity_id bigint null,
	identity_type varchar(200) null,
	resource_id bigint null
);

create table if not exists pipeline_repositories
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	password varchar(200) null,
	server_url varchar(200) null,
	type varchar(200) null,
	user_name varchar(200) null
);

create table if not exists pipelines
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	multi_branch tinyint default 0 null,
	project_id bigint null,
	repository_id bigint null,
	url varchar(200) null
);

create table if not exists plan_dependencies
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	project_id bigint null,
	source_id bigint null,
	target_id bigint null,
	type varchar(200) null
);

create table if not exists plans
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	deliverables json null,
	duration int null,
	finished tinyint(1) default 0 not null,
	item_no varchar(200) null,
	items json null,
	ordinary int null,
	owner_id bigint null,
	parent_id bigint null,
	plan_end_date datetime null,
	plan_start_date datetime null,
	progress int null,
	project_id bigint null,
	real_end_date datetime null,
	real_start_date datetime null,
	seq_number varchar(200) null,
	sprints json null,
	type varchar(200) null
);

create table if not exists product_lines
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	owner_id bigint null
);

create table if not exists products
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	parent_id bigint null,
	product_line_id bigint null,
	project_id bigint null
);

create table if not exists project_code_repositories
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	project_id bigint null,
	project_id_of_repository varchar(200) null,
	project_name_of_repository varchar(200) null,
	repository_id bigint null
);

create table if not exists project_pages
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	block_tracker_item_refs varchar(200) null,
	component_type varchar(200) null,
	content varchar(200) null,
	definition varchar(200) null,
	folder tinyint(1) default 0 not null,
	level int null,
	`order` int null,
	page_setting_trackers json null,
	parent_id bigint null,
	project_id bigint null,
	revision bigint null,
	smart_page_id bigint null,
	smart_doc_id bigint null,
	tracker_id bigint null,
	type varchar(200) null,
	url varchar(200) null,
	watchers json null
);

create table if not exists project_template
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	content varchar(200) null,
	location varchar(200) null,
	tracker_prefix_to_replace varchar(200) null
);

create table if not exists projects
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon longtext null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	category_id bigint null,
	end_date json null,
	key_name varchar(200) null,
	max_item_no bigint null,
	meaning varchar(200) null,
	owner_id bigint null,
	progress int null,
	start_date json null,
	status_id bigint null,
	template_name varchar(200) null
);

create table if not exists recent_projects
(
	id bigint not null
		primary key,
	name varchar(255) null,
	description varchar(255) null,
	icon varchar(255) null,
	user_id bigint not null comment '用户ID',
	project_id bigint not null comment '项目ID',
	last_access_date datetime not null comment '最后访问时间',
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null
);

create table if not exists report_groups
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	product_line_id bigint null
);

create table if not exists reports
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	group_id bigint null,
	product_line_id bigint null,
	smart_page_id bigint null
);

create table if not exists reqif_export_jobs
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	export_type int null,
	mappings json null,
	page_id bigint null,
	project_id bigint null,
	req_i_f_file_name varchar(200) null,
	req_i_f_zip_file_name varchar(200) null,
	req_i_f_zip_file_path varchar(200) null
);

create table if not exists reqif_export_templates
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	mappings json null,
	project_id bigint null,
	scope varchar(200) null
);

create table if not exists reqif_import_configs
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	project_id bigint null,
	rules json null,
	scope varchar(200) null
);

create table if not exists reqif_import_job_history
(
	id bigint not null
		primary key,
	object_id bigint not null,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	auto_number tinyint default 0 not null,
	blocks_json json null,
	file_path varchar(200) null,
	page_id bigint null,
	project_id bigint null,
	revision bigint null
);

create table if not exists reqif_import_jobs
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	auto_number tinyint default 0 not null,
	blocks_json json null,
	file_path varchar(200) null,
	page_id bigint null,
	project_id bigint null,
	revision bigint null
);

create table if not exists reviews
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	creator bigint null,
	deliverables json null,
	documents json null,
	dones json null,
	due_date varchar(200) null,
	milestones json null,
	pass_rate json null,
	project_id bigint null,
	project_plans json null,
	review_statuses json null,
	reviewers json null,
	sprints json null,
	status varchar(200) null,
	target_versions json null,
	tasks json null,
	tracker_items json null
);

create table if not exists role_members
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	owner_resource_id bigint null,
	role_id bigint null,
	scope varchar(200) null,
	user_id bigint null
);

create table if not exists roles
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	owner_resource_id bigint null,
	scope varchar(200) null,
	special_role tinyint(1) default 0 not null,
	special_role_type varchar(200) null
);

create table if not exists smart_pages
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	definition json null,
	object_id bigint null,
	scope varchar(200) null
);

create table if not exists sprints
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	duration int null,
	item_no varchar(200) null,
	items json null,
	meaning varchar(200) null,
	owner_id bigint null,
	plan_end_date datetime null,
	plan_start_date datetime null,
	progress int null,
	project_id bigint null,
	real_end_date json null,
	real_start_date json null,
	status_id bigint null,
	target_version_id bigint null
);

create table if not exists systems
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	login_logo varchar(200) null,
	logo varchar(200) null
);

create table if not exists target_versions
(
	id bigint not null primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	project_id bigint null
);

create table if not exists test_reports
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	owner_id bigint null,
	plan_end_date json null,
	plan_start_date json null,
	project_id bigint null,
	result_desc varchar(200) null,
	test_run_ids json null
);


create table if not exists test_results
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description text null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	executor_id bigint null,
	item_no varchar(200) null,
	result_desc varchar(200) null,
	result_id bigint null,
	test_case_id bigint null,
	test_run_id bigint null,
	test_step_results json null
);


create table if not exists test_runs
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	meaning varchar(200) null,
	owner_id bigint null,
	phase_id bigint null,
	plan_end_date json null,
	plan_start_date json null,
	project_id bigint null,
	sprint_id bigint null,
	status_id bigint null
);


create table if not exists tracker_commits
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	added json null,
	branch varchar(200) null,
	commit_id varchar(200) null,
	commit_message varchar(200) null,
	commit_on json null,
	commit_url varchar(200) null,
	committer varchar(200) null,
	item_id bigint null,
	modified json null,
	project_id bigint null,
	project_name_of_repository varchar(200) null,
	project_url varchar(200) null,
	removed json null,
	repository_id bigint null,
	repository_type varchar(200) null,
	status varchar(200) null,
	tracker_id bigint null
);


create table if not exists tracker_items
(
	id bigint not null primary key,
	name varchar(200) null,
	description longtext null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	assigned_date datetime null,
	assigned_to_id bigint null,
	close_date datetime null,
	estimate_working_hours decimal(10,6) null,
	item_no varchar(200) null,
	meaning_id bigint null,
	owner_id bigint null,
	parent_id bigint null,
	plan_end_date datetime null,
	plan_start_date datetime null,
	priority_id bigint null,
	progress int null,
	project_id bigint null,
	real_end_date datetime null,
	real_start_date datetime null,
	registered_working_hours decimal(10,6) null,
	related_wikis json null,
	remaining_working_hours decimal(10,6) null,
	revision bigint null,
	severity_id bigint null,
	sprint_id bigint null,
	status_id bigint null,
	tracker_id bigint null,
	`values` json null,
	watchers json null
);


create table if not exists tracker_items_history
(
	id bigint not null
		primary key,
	object_id bigint not null,
	name varchar(200) null,
	description longtext null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	assigned_date datetime null,
	assigned_to_id bigint null,
	attachments json null,
	close_date datetime null,
	estimate_working_hours int null,
	item_no varchar(200) null,
	meaning_id bigint null,
	owner_id bigint null,
	parent_id bigint null,
	plan_end_date datetime null,
	plan_start_date datetime null,
	priority_id bigint null,
	progress int null,
	project_id bigint null,
	real_end_date datetime null,
	real_start_date datetime null,
	registered_working_hours int null,
	related_wikis json null,
	remaining_working_hours int null,
	revision bigint null,
	sprint_id bigint null,
	status_id bigint null,
	tracker_id bigint null,
	severity_id bigint null,
	`values` json null,
	watchers json null
);


create table if not exists tracker_link_types
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	code varchar(200) null,
	opposite_name varchar(200) null,
	ordinary int null,
	parent tinyint(1) default 0 not null,
	project_id bigint null,
	rules json null,
	`system` tinyint(1) default 0 not null
);


create table if not exists tracker_links
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	link_type_id bigint null,
	source_item_id bigint null,
	target_item_id bigint null
);


create table if not exists tracker_merge_requests
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	assignees varchar(200) null,
	auditor varchar(200) null,
	item_id bigint null,
	merge_request_id varchar(200) null,
	merge_status varchar(200) null,
	project_id bigint null,
	repository_id bigint null,
	repository_type varchar(200) null,
	request_url varchar(200) null,
	reviewers varchar(200) null,
	source_branch varchar(200) null,
	status varchar(200) null,
	submit_user varchar(200) null,
	target_branch varchar(200) null,
	tracker_id bigint null
);


create table if not exists tracker_test_result_links
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	test_result_id bigint null,
	tracker_item_id bigint null
);

create table if not exists trackers
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	default_layout varchar(200) null,
	group_id bigint null,
	ordinary int null,
	project_id bigint null,
	tracker_fields json null,
	tracker_layouts json null,
	tracker_notification json null,
	tracker_state_transitions json null,
	tracker_statuses json null,
	tracker_type_id bigint null
);

create table if not exists users
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	dingtalk_open_id varchar(200) null,
	email varchar(200) null,
	fei_shu_open_id varchar(200) null,
	language varchar(200) null,
	last_login datetime null,
	login_name varchar(200) null,
	may_authorize json null,
	mobile_phone varchar(200) null,
	org_id bigint null,
	password varchar(200) null,
	remote_address varchar(200) null,
	disabled tinyint default 0 not null,
	user_code varchar(200) null,
	wechat_open_id varchar(200) null
);

create table if not exists views
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	display tinyint(1) default 0 not null,
	object_id bigint null,
	ordinary int null,
	`system` tinyint(1) default 0 not null,
	view_config json null,
	view_type varchar(200) null
);

create table if not exists word_import_config
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	project_id bigint null,
	rules json null,
	scope varchar(200) null
);

create table if not exists word_import_job_history
(
	id bigint not null
		primary key,
	object_id bigint not null,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	auto_number tinyint default 0 not null,
	blocks_json json null,
	file_path varchar(200) null,
	page_id bigint null,
	project_id bigint null,
	revision bigint null
);

create table if not exists word_import_jobs
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	auto_number tinyint default 0 not null,
	blocks_json json null,
	file_path varchar(200) null,
	page_id bigint null,
	project_id bigint null,
	revision bigint null
);

create table if not exists work_hours
(
	id bigint not null
		primary key,
	name varchar(200) null,
	description varchar(500) null,
	icon varchar(500) null,
	deleted tinyint default 0 not null,
	create_by bigint null,
	create_date datetime null,
	last_modified_by bigint null,
	last_modified_date datetime null,
	actual_hour double null,
	member_id bigint null,
	object_id bigint null,
	start_time datetime null
);

create or replace view v_tracker_items as select `items`.`id`                                                            AS `id`,
       `items`.`name`                                                                                                    AS `name`,
       `items`.`description`                                                                                             AS `description`,
       `items`.`icon`                                                                                                    AS `icon`,
       `items`.`deleted`                                                                                                 AS `deleted`,
       `items`.`create_by`                                                                                               AS `create_by`,
       `u1`.`name`                                                                                                       AS `create_by_name`,
       `items`.`create_date`                                                                                             AS `create_date`,
       `items`.`last_modified_by`                                                                                        AS `last_modified_by`,
       `u2`.`name`                                                                                                       AS `last_modified_by_name`,
       `items`.`last_modified_date`                                                                                      AS `last_modified_date`,
       `items`.`assigned_date`                                                                                           AS `assigned_date`,
       `items`.`assigned_to_id`                                                                                          AS `assigned_to_id`,
       `u3`.`name`                                                                                                       AS `assigned_to_name`,
       `items`.`close_date`                                                                                              AS `close_date`,
       `items`.`estimate_working_hours`                                                                                  AS `estimate_working_hours`,
       `items`.`item_no`                                                                                                 AS `item_no`,
       `items`.`meaning_id`                                                                                              AS `meaning_id`,
       `meaning`.`name`                                                                                                  AS `meaning_name`,
       `items`.`owner_id`                                                                                                AS `owner_id`,
       `u4`.`name`                                                                                                       AS `owner_name`,
       `items`.`parent_id`                                                                                               AS `parent_id`,
       `items`.`plan_end_date`                                                                                           AS `plan_end_date`,
       `items`.`plan_start_date`                                                                                         AS `plan_start_date`,
       `items`.`priority_id`                                                                                             AS `priority_id`,
       `priority_enum`.`name`                                                                                            AS `priority_name`,
       `items`.`progress`                                                                                                AS `progress`,
       `items`.`project_id`                                                                                              AS `project_id`,
       `p`.`name`                                                                                                        AS `project_name`,
       `items`.`real_end_date`                                                                                           AS `real_end_date`,
       `items`.`real_start_date`                                                                                         AS `real_start_date`,
       `items`.`registered_working_hours`                                                                                AS `registered_working_hours`,
       `items`.`related_wikis`                                                                                           AS `related_wikis`,
       `items`.`remaining_working_hours`                                                                                 AS `remaining_working_hours`,
       `items`.`revision`                                                                                                AS `revision`,
       `items`.`severity_id`                                                                                             AS `severity_id`,
       `severity`.`name`                                                                                                AS `severity_name`,
       `items`.`sprint_id`                                                                                               AS `sprint_id`,
       `sprint`.`name`                                                                                                   AS `sprint_name`,
       `items`.`status_id`                                                                                               AS `status_id`,
       `status`.`name`                                                                                                   AS `status_name`,
       `items`.`tracker_id`                                                                                              AS `tracker_id`,
       (case `items`.`tracker_id` when -(1) then '章节' when -(2) then '标题' when -(3) then '段落' else `tracker`.`name` end) AS `tracker_name`,
       `items`.`values`                                                                                                  AS `values`,
       `items`.`watchers`                                                                                                AS `watchers`
from (((((((((((`tracker_items` `items` left join `projects` `p` on ((`items`.`project_id` = `p`.`id`))) left join `trackers` `tracker` on ((`items`.`tracker_id` = `tracker`.`id`))) left join `enum_items` `priority_enum` on ((`items`.`priority_id` = `priority_enum`.`id`))) left join `enum_items` `meaning` on ((`items`.`meaning_id` = `meaning`.`id`))) left join `enum_items` `severity` on ((`items`.`severity_id` = `severity`.`id`))) left join `enum_items` `status` on ((`items`.`status_id` = `status`.`id`))) left join `sprints` `sprint` on ((`items`.`sprint_id` = `sprint`.`id`))) left join `users` `u1` on ((`items`.`create_by` = `u1`.`id`))) left join `users` `u2` on ((`items`.`last_modified_by` = `u2`.`id`))) left join `users` `u3` on ((`items`.`assigned_to_id` = `u3`.`id`)))
         left join `users` `u4` on ((`items`.`owner_id` = `u4`.`id`)))
where (`items`.`deleted` = 0);

create or replace  view v_tracker_items_history as select `items`.`id`                                                   AS `id`,
       `items`.`object_id`                                                                                               AS `object_id`,
       `items`.`name`                                                                                                    AS `name`,
       `items`.`description`                                                                                             AS `description`,
       `items`.`icon`                                                                                                    AS `icon`,
       `items`.`deleted`                                                                                                 AS `deleted`,
       `items`.`create_by`                                                                                               AS `create_by`,
       `u1`.`name`                                                                                                       AS `create_by_name`,
       `items`.`create_date`                                                                                             AS `create_date`,
       `items`.`last_modified_by`                                                                                        AS `last_modified_by`,
       `u2`.`name`                                                                                                       AS `last_modified_by_name`,
       `items`.`last_modified_date`                                                                                      AS `last_modified_date`,
       `items`.`assigned_date`                                                                                           AS `assigned_date`,
       `items`.`assigned_to_id`                                                                                          AS `assigned_to_id`,
       `u3`.`name`                                                                                                       AS `assigned_to_name`,
       `items`.`close_date`                                                                                              AS `close_date`,
       `items`.`estimate_working_hours`                                                                                  AS `estimate_working_hours`,
       `items`.`item_no`                                                                                                 AS `item_no`,
       `items`.`meaning_id`                                                                                              AS `meaning_id`,
       `meaning`.`name`                                                                                                  AS `meaning_name`,
       `items`.`owner_id`                                                                                                AS `owner_id`,
       `u4`.`name`                                                                                                       AS `owner_name`,
       `items`.`parent_id`                                                                                               AS `parent_id`,
       `items`.`plan_end_date`                                                                                           AS `plan_end_date`,
       `items`.`plan_start_date`                                                                                         AS `plan_start_date`,
       `items`.`priority_id`                                                                                             AS `priority_id`,
       `priority_enum`.`name`                                                                                            AS `priority_name`,
       `items`.`progress`                                                                                                AS `progress`,
       `items`.`project_id`                                                                                              AS `project_id`,
       `p`.`name`                                                                                                        AS `project_name`,
       `items`.`real_end_date`                                                                                           AS `real_end_date`,
       `items`.`real_start_date`                                                                                         AS `real_start_date`,
       `items`.`registered_working_hours`                                                                                AS `registered_working_hours`,
       `items`.`related_wikis`                                                                                           AS `related_wikis`,
       `items`.`remaining_working_hours`                                                                                 AS `remaining_working_hours`,
       `items`.`revision`                                                                                                AS `revision`,
       `items`.`severity_id`                                                                                             AS `severity_id`,
       `severity`.`name`                                                                                                AS `severity_name`,
       `items`.`sprint_id`                                                                                               AS `sprint_id`,
       `sprint`.`name`                                                                                                   AS `sprint_name`,
       `items`.`status_id`                                                                                               AS `status_id`,
       `status`.`name`                                                                                                   AS `status_name`,
       `items`.`tracker_id`                                                                                              AS `tracker_id`,
       (case `items`.`tracker_id` when -(1) then '章节' when -(2) then '标题' when -(3) then '段落' else `tracker`.`name` end) AS `tracker_name`,
       `items`.`values`                                                                                                  AS `values`,
       `items`.`watchers`                                                                                                AS `watchers`
from (((((((((((`tracker_items_history` `items` left join `projects` `p` on ((`items`.`project_id` = `p`.`id`))) left join `trackers` `tracker` on ((`items`.`tracker_id` = `tracker`.`id`))) left join `enum_items` `priority_enum` on ((`items`.`priority_id` = `priority_enum`.`id`))) left join `enum_items` `meaning` on ((`items`.`meaning_id` = `meaning`.`id`))) left join `enum_items` `severity` on ((`items`.`severity_id` = `severity`.`id`))) left join `enum_items` `status` on ((`items`.`status_id` = `status`.`id`))) left join `sprints` `sprint` on ((`items`.`sprint_id` = `sprint`.`id`))) left join `users` `u1` on ((`items`.`create_by` = `u1`.`id`))) left join `users` `u2` on ((`items`.`last_modified_by` = `u2`.`id`))) left join `users` `u3` on ((`items`.`assigned_to_id` = `u3`.`id`)))
         left join `users` `u4` on ((`items`.`owner_id` = `u4`.`id`)))
where (`items`.`deleted` = 0);



create  function product_line_has_permission(productLineId bigint, userId bigint, permission varchar(200))
returns tinyint
READS SQL DATA
begin
    DECLARE result tinyint DEFAULT 0;
    with t as (
        select product_lines.id,
               product_lines.owner_id,
               p.name,
               p.identity_id,
               p.identity_type,
               roles.id as role_id,
               roles.special_role_type,
               rm.user_id
        from product_lines
                 left join permissions p on product_lines.id = p.resource_id
                 left join roles on roles.owner_resource_id = product_lines.id and roles.id = p.identity_id and
                                    (p.identity_type = 'IDENTITY_ROLE' or p.identity_type = 'IDENTITY_SPECIAL_ROLE')
                 left join role_members rm on roles.id = rm.role_id

        where p.name = permission
          and product_lines.id = productLineId
    )
    select 1
    into result
    where exists(select 1
                 from t
                 where (t.identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'PRODUCT_LINE_OWNER' and t.owner_id = userId)
                    or (t.identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'ALL_USERS')
                    or (t.identity_type = 'IDENTITY_USER' and identity_id = userId)
                    or (t.identity_type = 'IDENTITY_ROLE' and user_id = userId)
              );

    return result;
end;


create function project_has_permission(projectId bigint, userId bigint, permission varchar(200))
returns tinyint
READS SQL DATA
begin
    DECLARE result tinyint DEFAULT 0;
    with t as (
        select projects.id,
               projects.owner_id,
               p.name,
               p.identity_id,
               p.identity_type,
               roles.id as role_id,
               roles.special_role_type,
               rm.user_id
        from projects
                 left join permissions p on projects.id = p.resource_id
                 left join roles on roles.owner_resource_id = projects.id and roles.id = p.identity_id and
                                    (p.identity_type = 'IDENTITY_ROLE' or p.identity_type = 'IDENTITY_SPECIAL_ROLE')
                 left join role_members rm on roles.id = rm.role_id

        where p.name = permission
          and projects.id = projectId
    )
    select 1
    into result
    where exists(select 1
                 from t
                 where (t.identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'PROJECT_OWNER' and t.owner_id = userId)
                    or (t.identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'PROJECT_ALL_MEMBERS' and t.owner_id = userId)
                    or (t.identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'ALL_USERS')
                    or (t.identity_type = 'IDENTITY_USER' and identity_id = userId)
                    or (t.identity_type = 'IDENTITY_ROLE' and user_id = userId)
              );

    return result;
end;


create function tracker_item_has_permission(itemId bigint, userId bigint, permission varchar(200))
returns tinyint
READS SQL DATA
begin
    DECLARE result tinyint DEFAULT 0;
    with granted as (
    select p.identity_id, p.identity_type
    from permissions p
    where p.deleted = 0
      and p.name = permission
      and p.resource_id in (select tracker_id from tracker_items where id = itemId)
),
     grantedSpecial as (
         select granted.*, roles.special_role_type
         from granted
                  left join roles on roles.id = granted.identity_id and granted.identity_type = 'IDENTITY_SPECIAL_ROLE'
     ),
     t3 as (select 1
            from (
                     select cast(json_extract(tracker_items.watchers, '$[0].id') as UNSIGNED) as identity_id,
                            json_unquote(json_extract(tracker_items.watchers, '$[0].type'))   as identity_type
                     from tracker_items
                     where tracker_items.id = itemId
                       and tracker_items.tracker_id > 0
                       and JSON_LENGTH(tracker_items.watchers) > 0
                 ) t2
                     left join roles on roles.id = t2.identity_id and t2.identity_type = 'IDENTITY_SPECIAL_ROLE'
            where (t2.identity_type = 'IDENTITY_SPECIAL_ROLE' and
                   special_role_type = 'PROJECT_OWNER' and
                   exists(
                           select 1
                           from projects
                           where projects.id in (select project_id from tracker_items items where items.id = itemId)
                             and projects.owner_id = 1))
               or (t2.identity_type = 'IDENTITY_SPECIAL_ROLE' and
                   special_role_type = 'PROJECT_ALL_MEMBERS' and
                   exists(select 1
                          from role_members
                          where role_members.owner_resource_id in
                                (select project_id from tracker_items items where items.id = itemId)
                            and role_members.user_id = 1))
               or (t2.identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'ALL_USERS')
               or (t2.identity_type = 'IDENTITY_SPECIAL_ROLE' and
                   special_role_type = 'TRACKER_OWNER' and
                   exists(select 1 from tracker_items items where items.id = itemId and items.owner_id = userId))
               or (t2.identity_type = 'IDENTITY_SPECIAL_ROLE' and
                   special_role_type = 'TRACKER_CREATOR' and
                   exists(select 1 from tracker_items items where items.id = itemId and items.create_by = userId))
               or (t2.identity_type = 'IDENTITY_USER' and t2.identity_id = 1)
               or (t2.identity_type = 'IDENTITY_ROLE' and exists(select 1
                                                                 from role_members
                                                                 where role_members.owner_resource_id in
                                                                       (select project_id from tracker_items items where items.id = itemId)
                                                                   and role_members.role_id = t2.identity_id
                                                                   and role_members.user_id = userId))
     )
select 1
into  result
where exists(
              select 1
              from grantedSpecial
              where (identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'PROJECT_OWNER' and exists(
                      select 1
                      from projects
                      where projects.id in (select project_id from tracker_items items where items.id = itemId)
                        and projects.owner_id = 1)
                  or (identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'PROJECT_ALL_MEMBERS' and exists(select 1
                                                                                                                       from role_members
                                                                                                                       where role_members.owner_resource_id in
                                                                                                                             (select project_id from tracker_items items where items.id = itemId)
                                                                                                                         and role_members.user_id = userId))
                  or (identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'ALL_USERS')
                  or (identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'TRACKER_OWNER' and
                      exists(select 1 from tracker_items items where items.id = itemId and items.owner_id = userId))
                  or (identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'TRACKER_CREATOR' and
                      exists(select 1 from tracker_items items where items.id = itemId and items.create_by = userId))
                  or (identity_type = 'IDENTITY_SPECIAL_ROLE' and special_role_type = 'TRACKER_WATCHER' and exists(select 1
                                                                                                                   from t3))
                  or (identity_type = 'IDENTITY_USER' and identity_id = 1)
                  or (identity_type = 'IDENTITY_ROLE' and exists(select 1
                                                                 from role_members
                                                                 where role_members.owner_resource_id in
                                                                       (select project_id from tracker_items items where items.id = itemId)
                                                                   and role_members.role_id = grantedSpecial.identity_id
                                                                   and role_members.user_id = userId))
                        )
          );

    return result;
end;




