UPDATE trackers
SET tracker_fields = JSON_REMOVE(
        tracker_fields,
        ( SELECT REPLACE ( REPLACE ( JSON_SEARCH( tracker_fields, 'one', 'sprintId', NULL, '$[*].systemProperty' ), '.systemProperty', '' ), '"', '' ) )
                     )
WHERE
    JSON_SEARCH( tracker_fields, 'one', 'sprintId', NULL, '$[*].systemProperty' ) IS NOT NULL