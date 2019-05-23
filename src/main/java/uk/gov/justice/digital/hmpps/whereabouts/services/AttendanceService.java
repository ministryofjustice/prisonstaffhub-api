package uk.gov.justice.digital.hmpps.whereabouts.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.digital.hmpps.whereabouts.dto.AbsentReasonsDto;
import uk.gov.justice.digital.hmpps.whereabouts.dto.AttendanceDto;
import uk.gov.justice.digital.hmpps.whereabouts.dto.CreateAttendanceDto;
import uk.gov.justice.digital.hmpps.whereabouts.model.AbsentReason;
import uk.gov.justice.digital.hmpps.whereabouts.model.Attendance;
import uk.gov.justice.digital.hmpps.whereabouts.model.TimePeriod;
import uk.gov.justice.digital.hmpps.whereabouts.repository.AttendanceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final NomisService nomisService;
    private final static NomisEventOutcomeMapper nomisEventOutcomeMapper = new NomisEventOutcomeMapper();

    public AttendanceService(final AttendanceRepository attendanceRepository, final NomisService nomisService) {
        this.attendanceRepository = attendanceRepository;
        this.nomisService = nomisService;
    }

    @Transactional
    public void createOffenderAttendance(final CreateAttendanceDto updatedAttendance) {
        attendanceRepository.save(Attendance
                .builder()
                .eventLocationId(updatedAttendance.getEventLocationId())
                .eventDate(updatedAttendance.getEventDate())
                .eventId(updatedAttendance.getEventId())
                .offenderBookingId(updatedAttendance.getBookingId())
                .period(updatedAttendance.getPeriod())
                .paid(updatedAttendance.isPaid())
                .attended(updatedAttendance.isAttended())
                .prisonId(updatedAttendance.getPrisonId())
                .absentReason(updatedAttendance.getAbsentReason())
                .comments(updatedAttendance.getComments())
                .build());

        if (updatedAttendance.getAbsentReason() != null && AbsentReason.getIepTriggers().contains(updatedAttendance.getAbsentReason())) {
            nomisService.postCaseNote(
                    updatedAttendance.getBookingId(),
                    "NEG",//"Negative Behaviour"
                    "IEP_WARN", //"IEP Warning",
                    StringUtils.defaultIfEmpty(updatedAttendance.getComments(),
                            "Refused to attend activity / education."),
                    LocalDateTime.now()
            );
        }

        final var eventOutcome = nomisEventOutcomeMapper.getEventOutcome(
                updatedAttendance.getAbsentReason(),
                updatedAttendance.isAttended(),
                updatedAttendance.isPaid());

        nomisService.updateAttendance(
                updatedAttendance.getOffenderNo(),
                updatedAttendance.getEventId(),
                eventOutcome);
    }

    public Set<AttendanceDto> getAttendance(final String prisonId, final Long eventLocationId, final LocalDate date, final TimePeriod period) {
        final var attendance = attendanceRepository
                .findByPrisonIdAndEventLocationIdAndEventDateAndPeriod(prisonId, eventLocationId, date, period);

        return attendance
                .stream()
                .map(attendanceData -> AttendanceDto.builder()
                        .id(attendanceData.getId())
                        .eventDate(attendanceData.getEventDate())
                        .eventId(attendanceData.getEventId())
                        .bookingId(attendanceData.getOffenderBookingId())
                        .period(attendanceData.getPeriod())
                        .paid(attendanceData.isPaid())
                        .attended(attendanceData.isAttended())
                        .prisonId(attendanceData.getPrisonId())
                        .absentReason(attendanceData.getAbsentReason())
                        .eventLocationId(attendanceData.getEventLocationId())
                        .comments(attendanceData.getComments())
                        .build())
                  .collect(Collectors.toSet());
    }

    public AbsentReasonsDto getAbsenceReasons() {
        return new AbsentReasonsDto(AbsentReason.getPaidReasons(), AbsentReason.getUnpaidReasons());
    }
}