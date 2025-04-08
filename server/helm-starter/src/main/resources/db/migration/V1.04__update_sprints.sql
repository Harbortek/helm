ALTER TABLE tracker_items
    DROP COLUMN sprint_id;

ALTER TABLE tracker_items_history
    DROP COLUMN sprint_id;

ALTER TABLE sprints
    MODIFY COLUMN real_start_date DATETIME,
    MODIFY COLUMN real_end_date DATETIME;