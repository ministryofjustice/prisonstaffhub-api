package uk.gov.justice.digital.hmpps.whereabouts.model

import java.time.LocalDateTime

data class PrisonAppointment(
  val agencyId: String,
  val bookingId: Long,
  val endTime: LocalDateTime,
  val eventId: Long,
  val eventLocationId: Long,
  val eventSubType: String,
  val startTime: LocalDateTime,
)