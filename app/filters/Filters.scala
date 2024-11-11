package filters

import play.api.http.DefaultHttpFilters
import play.filters.csrf.CSRFFilter

import javax.inject.Inject

class Filters @Inject() (accessLogFilter: AccessLogFilter, csrfFilter: CSRFFilter) extends DefaultHttpFilters(accessLogFilter, csrfFilter) {}
