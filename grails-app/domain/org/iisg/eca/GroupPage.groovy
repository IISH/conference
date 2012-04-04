package org.iisg.eca

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all existing relationships between groups and pages
 */
class GroupPage extends DefaultDomain implements Serializable {
	Group group
	Page page
	boolean showInMenu = true

    static belongsTo = [Group, Page]

    static mapping = {
		table 'groups_pages'
		version false
        id composite: ['group', 'page']

        group       column: 'group_id'
        page        column: 'page_id'
        showInMenu  column: 'show_in_menu'
	}

    @Override
	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append group
		builder.append page
		builder.toHashCode()
	}

    @Override
	boolean equals(other) {
		if (other == null) {
            return false
        }

		def builder = new EqualsBuilder()
		builder.append group, other.group
		builder.append page, other.page
		builder.isEquals()
	}
}
