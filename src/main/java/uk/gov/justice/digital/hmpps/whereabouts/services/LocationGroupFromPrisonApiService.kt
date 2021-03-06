package uk.gov.justice.digital.hmpps.whereabouts.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.whereabouts.model.Location
import uk.gov.justice.digital.hmpps.whereabouts.model.LocationGroup
import java.util.function.Predicate

@Service("defaultLocationGroupService")
class LocationGroupFromPrisonApiService(private val prisonApiService: PrisonApiService) : LocationGroupService {

  override fun getLocationGroupsForAgency(agencyId: String): List<LocationGroup> = getLocationGroups(agencyId)

  override fun getLocationGroups(agencyId: String): List<LocationGroup> = prisonApiService.getLocationGroups(agencyId)

  override fun locationGroupFilter(agencyId: String, groupName: String): Predicate<Location> {
    val prefixToMatch = "$agencyId-${groupName.replace('_', '-')}-"
    return Predicate { it.locationPrefix.startsWith(prefixToMatch) }
  }
}
