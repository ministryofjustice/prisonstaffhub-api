DROP TABLE IF EXISTS CELL_MOVE_REASON;

CREATE TABLE CELL_MOVE_REASON
(
  BOOKING_ID BIGINT NOT NULL,
  BED_ASSIGNMENT_SEQUENCE BIGINT NOT NULL,
  CASE_NOTE_ID INT NOT NULL,
  PRIMARY KEY (BOOKING_ID, BED_ASSIGNMENT_SEQUENCE)
);

COMMENT ON TABLE CELL_MOVE_REASON IS 'Links a case note with an cell move';
