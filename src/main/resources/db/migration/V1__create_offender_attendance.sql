DROP TABLE IF EXISTS OFFENDER_ATTENDANCE;

CREATE TABLE OFFENDER_ATTENDANCE
(
  ID                              SERIAL PRIMARY KEY,
  OFFENDER_BOOKING_ID             BIGINT          NOT NULL,
  EVENT_ID                        BIGINT          NOT NULL,
  EVENT_DATE                      DATE            NOT NULL,
  EVENT_LOCATION_ID               BIGINT          NOT NULL,
  PERIOD                          VARCHAR(2)      NOT NULL,
  PRISON_ID                       VARCHAR(6)      NOT NULL,
  ATTENDED                        BOOLEAN,
  PAID                            BOOLEAN,
  ABSENT_REASON                   VARCHAR(30),
);

COMMENT ON TABLE OFFENDER_ATTENDANCE IS 'Records the attendance at an event for an offender';

CREATE INDEX OFFENDER_ATTENDANCE_ON_NOX ON OFFENDER_ATTENDANCE (OFFENDER_BOOKING_ID, EVENT_DATE);
CREATE INDEX OFFENDER_ATTENDANCE_AI_IDX ON OFFENDER_ATTENDANCE (PRISON_ID);
CREATE INDEX OFFENDER_ATTENDANCE_LOC_IDX ON OFFENDER_ATTENDANCE (PRISON_ID, EVENT_LOCATION_ID, EVENT_DATE, PERIOD);

