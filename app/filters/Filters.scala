package filters

import play.api.http.DefaultHttpFilters
import play.filters.csrf.CSRFFilter

import javax.inject.Inject

class Filters @Inject() (
  accessLog: AccessLog,
  csrfFilter: CSRFFilter
) extends DefaultHttpFilters(
  accessLog,
  csrfFilter
) {}
