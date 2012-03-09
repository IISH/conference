--
-- Table structure for table `conferences`
--

CREATE TABLE IF NOT EXISTS `conferences` (
  `id` bigint(20) NOT NULL auto_increment,
  `short_name` varchar(20) collate utf8_unicode_ci NOT NULL,
  `long_name` varchar(50) collate utf8_unicode_ci NOT NULL,
  `type` varchar(20) collate utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `dates`
--

CREATE TABLE IF NOT EXISTS `dates` (
  `id` bigint(20) NOT NULL auto_increment,
  `conference_id` bigint(20) NOT NULL,
  `year_code` varchar(20) collate utf8_unicode_ci NOT NULL,
  `start_date` date default NULL,
  `end_date` date default NULL,
  `date_as_text` varchar(100) collate utf8_unicode_ci NOT NULL,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL,
  `long_description` text collate utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `dates_conference_id_idx` (`conference_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint(20) NOT NULL auto_increment,
  `role` varchar(20) collate utf8_unicode_ci NOT NULL,
  `full_rights` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `type_auth`
--

CREATE TABLE IF NOT EXISTS `type_auth` (
  `id` bigint(20) NOT NULL auto_increment,
  `shortcode` varchar(10) collate utf8_unicode_ci NOT NULL,
  `connectiestring` varchar(30) collate utf8_unicode_ci default NULL,
  `ww_bewaren` tinyint(1) NOT NULL default '0',
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `shortcode` (`shortcode`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL auto_increment,
  `email` varchar(30) collate utf8_unicode_ci NOT NULL,
  `full_name` varchar(30) collate utf8_unicode_ci NOT NULL,
  `institute` varchar(50) collate utf8_unicode_ci NOT NULL,
  `country` varchar(5) collate utf8_unicode_ci NOT NULL,
  `language` varchar(10) collate utf8_unicode_ci NOT NULL,
  `encrypted_password` varchar(128) collate utf8_unicode_ci NOT NULL,
  `salt` varchar(26) collate utf8_unicode_ci default NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users_roles`
--

CREATE TABLE IF NOT EXISTS `users_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `conference_id` bigint(20) default NULL,
  PRIMARY KEY  (`user_id`,`role_id`),
  KEY `users_roles_role_id_idx` (`role_id`),
  KEY `users_roles_conference_id_idx` (`conference_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users_type_auth`
--

CREATE TABLE IF NOT EXISTS `users_type_auth` (
  `user_id` bigint(20) NOT NULL,
  `type_auth_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`user_id`,`type_auth_id`),
  KEY `users_type_auth_type_auth_id_idx` (`type_auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Constraints for table `dates`
--

ALTER TABLE `dates`
  ADD CONSTRAINT `dates_conference_id_fk` FOREIGN KEY (`conference_id`) REFERENCES `conferences` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
  
-- --------------------------------------------------------

--
-- Constraints for table `users_roles`
--

ALTER TABLE `users_roles`
  ADD CONSTRAINT `users_roles_conference_id_fk` FOREIGN KEY (`conference_id`) REFERENCES `conferences` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `users_roles_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `users_roles_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- --------------------------------------------------------

--
-- Constraints for table `users_type_auth`
--

ALTER TABLE `users_type_auth`
  ADD CONSTRAINT `users_type_auth_type_auth_id_fk` FOREIGN KEY (`type_auth_id`) REFERENCES `type_auth` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
