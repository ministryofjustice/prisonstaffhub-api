package uk.gov.justice.digital.hmpps.whereabouts.services;

import org.springframework.stereotype.Service;
import uk.gov.justice.digital.hmpps.whereabouts.dto.AbsentReasonsDto;
import uk.gov.justice.digital.hmpps.whereabouts.dto.AttendanceDto;
import uk.gov.justice.digital.hmpps.whereabouts.dto.CreateAttendanceDto;
import uk.gov.justice.digital.hmpps.whereabouts.dto.UpdateAttendanceDto;
import uk.gov.justice.digital.hmpps.whereabouts.model.AbsentReason;
import uk.gov.justice.digital.hmpps.whereabouts.model.Attendance;
import uk.gov.justice.digital.hmpps.whereabouts.model.TimePeriod;
import uk.gov.justice.digital.hmpps.whereabouts.repository.AttendanceRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AttendanceService {
    private final static NomisEventOutcomeMapper nomisEventOutcomeMapper = new NomisEventOutcomeMapper();

    private final AttendanceRepository attendanceRepository;
    private final NomisService nomisService;
    private final EntityManager em;


    public AttendanceService(final AttendanceRepository attendanceRepository, final NomisService nomisService, EntityManager em) {
        this.attendanceRepository = attendanceRepository;
        this.nomisService = nomisService;
        this.em = em;
    }

    @Transactional
    public void createAttendance(final CreateAttendanceDto attendanceDto) {

        Attendance attendance = attendanceRepository.save(toAttendance(attendanceDto));
        em.flush();
        applyAttendanceWorkflow(attendance);
    }

    @Transactional
    public void updateAttendance(long id, UpdateAttendanceDto attendanceDto) throws AttendanceNotFound {

        final var attendance = attendanceRepository.findById(id)
                .orElseThrow(AttendanceNotFound::new);

        attendance.setAttended(attendanceDto.getAttended());
        attendance.setPaid(attendanceDto.getPaid());
        attendance.setAbsentReason(attendanceDto.getAbsentReason());
        attendance.setComments(attendanceDto.getComments());

        applyAttendanceWorkflow(attendance);
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
                        .paid(attendanceData.getPaid())
                        .attended(attendanceData.getAttended())
                        .prisonId(attendanceData.getPrisonId())
                        .absentReason(attendanceData.getAbsentReason())
                        .eventLocationId(attendanceData.getEventLocationId())
                        .comments(attendanceData.getComments())
                        .createUserId(attendanceData.getCreateUserId())
                        .createDateTime(attendanceData.getCreateDateTime())
                        .caseNoteId(attendanceData.getCaseNoteId())
                        .build())
                  .collect(Collectors.toSet());
    }

    public AbsentReasonsDto getAbsenceReasons() {
        return new AbsentReasonsDto(AbsentReason.getPaidReasons(), AbsentReason.getUnpaidReasons());
    }


    private void applyAttendanceWorkflow(Attendance attendance) {
        final var eventOutcome = nomisEventOutcomeMapper.getEventOutcome(
                attendance.getAbsentReason(),
                attendance.getAttended(),
                attendance.getPaid());

        nomisService.putAttendance(attendance.getOffenderBookingId(), attendance.getEventId(), eventOutcome);

        addCaseNoteIfRequired(attendance).ifPresent(attendance::setCaseNoteId);
    }

     private Optional<Long> addCaseNoteIfRequired(Attendance attendance) {
        if (attendance.getCaseNoteId() == null && attendance.getAbsentReason() != null && AbsentReason.getIepTriggers().contains(attendance.getAbsentReason())) {
            final var caseNote = nomisService.postCaseNote(
                    attendance.getOffenderBookingId(),
                    "NEG",//"Negative Behaviour"
                    "IEP_WARN", //"IEP Warning",
                    attendance.getComments(),
                    LocalDateTime.now());
             return Optional.of(caseNote.getCaseNoteId());
        }

        return Optional.empty();
    }

    private Attendance toAttendance(CreateAttendanceDto attendanceDto) {
         return Attendance
                 .builder()
                 .eventLocationId(attendanceDto.getEventLocationId())
                 .eventDate(attendanceDto.getEventDate())
                 .eventId(attendanceDto.getEventId())
                 .offenderBookingId(attendanceDto.getBookingId())
                 .period(attendanceDto.getPeriod())
                 .paid(attendanceDto.getPaid())
                 .attended(attendanceDto.getAttended())
                 .prisonId(attendanceDto.getPrisonId())
                 .absentReason(attendanceDto.getAbsentReason())
                 .comments(attendanceDto.getComments())
                 .build();
     }
}
