package uk.gov.justice.digital.hmpps.whereabouts.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@ApiModel(description = "Video Link Booking details")
data class VideoLinkBookingSpecification(

  @ApiModelProperty(value = "Offender booking Id", example = "1")
  @field:NotNull
  val bookingId: Long?,

  @ApiModelProperty(
    value = "The location of the court that requires the appointment",
    example = "York Crown Court",
    required = true
  )
  @NotEmpty
  val court: String,

  @ApiModelProperty(value = "Booking placed by the court", required = true)
  @NotNull
  val madeByTheCourt: Boolean?,

  @ApiModelProperty(value = "Free text comments", example = "Requires special access")
  val comment: String? = null,

  @ApiModelProperty(value = "Pre-hearing appointment")
  @Valid
  val pre: VideoLinkAppointmentSpecification? = null,

  @ApiModelProperty(value = "Main appointment", required = true)
  @field:Valid
  val main: VideoLinkAppointmentSpecification,

  @ApiModelProperty(value = "Post-hearing appointment")
  @Valid
  val post: VideoLinkAppointmentSpecification? = null
)
