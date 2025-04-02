alter table target_versions add column plan_start_date datetime;
alter table target_versions add column plan_end_date datetime;
alter table target_versions add column real_start_date datetime;
alter table target_versions add column real_end_date datetime;
alter table target_versions add column progress int;
alter table target_versions add column status_id bigint;
alter table target_versions add column total_working_hours decimal(10,6);
alter table target_versions add column completed_working_hours decimal(10,6);
alter table target_versions add column remaining_working_hours decimal(10,6);


alter table sprints add column total_working_hours decimal(10,6);
alter table sprints add column completed_working_hours decimal(10,6);
alter table sprints add column remaining_working_hours decimal(10,6);