-- phpMyAdmin SQL Dump
-- version 2.11.10
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 20, 2012 at 04:40 PM
-- Server version: 5.0.86
-- PHP Version: 5.2.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `eca`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin_pages`
--

CREATE TABLE IF NOT EXISTS `admin_pages` (
  `admin_page_id` bigint(20) NOT NULL auto_increment,
  `page_id` bigint(20) NOT NULL,
  `event_id` bigint(20) default NULL,
  `show_in_menu` tinyint(1) NOT NULL default '1',
  PRIMARY KEY  (`admin_page_id`),
  KEY `admin_pages_page_id_idx` (`page_id`),
  KEY `admin_pages_event_id_idx` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

--
-- Dumping data for table `admin_pages`
--


-- --------------------------------------------------------

--
-- Table structure for table `countries`
--

CREATE TABLE IF NOT EXISTS `countries` (
  `country_id` bigint(20) NOT NULL auto_increment,
  `tld` varchar(2) collate utf8_unicode_ci NOT NULL,
  `name_english` varchar(50) collate utf8_unicode_ci NOT NULL,
  `name_dutch` varchar(50) collate utf8_unicode_ci NOT NULL,
  `remarks` text collate utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`country_id`),
  UNIQUE KEY `tld` (`tld`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=255 ;

--
-- Dumping data for table `countries`
--

INSERT INTO `countries` (`country_id`, `tld`, `name_english`, `name_dutch`, `remarks`, `enabled`, `deleted`) VALUES
(1, 'ac', 'Ascension Island', 'Ascension (eiland)', NULL, 1, 0),
(2, 'ad', 'Andorra', 'Andorra', NULL, 1, 0),
(3, 'ae', 'United Arab Emirates', 'Verenigde Arabische Emiraten (Abu Dhabi, Dubai)', NULL, 1, 0),
(4, 'af', 'Afghanistan', 'Afghanistan', NULL, 1, 0),
(5, 'ag', 'Antigua and Barbuda', 'Antigua en Barbuda', NULL, 1, 0),
(6, 'ai', 'Anguilla', 'Anguilla', NULL, 1, 0),
(7, 'al', 'Albania', 'Albanië', NULL, 1, 0),
(8, 'am', 'Armenia', 'Armenië', NULL, 1, 0),
(9, 'an', 'Netherlands Antilles', 'Nederlandse Antillen', NULL, 1, 0),
(10, 'ao', 'Angola', 'Angola', NULL, 1, 0),
(11, 'aq', 'Antarctica', 'Antarctica', NULL, 1, 0),
(12, 'ar', 'Argentina', 'Argentinië', NULL, 1, 0),
(13, 'as', 'American Samoa', 'Amerikaans Samoa', NULL, 1, 0),
(14, 'at', 'Austria', 'Oostenrijk', NULL, 1, 0),
(15, 'au', 'Australia', 'Australië', NULL, 1, 0),
(16, 'aw', 'Aruba', 'Aruba', NULL, 1, 0),
(17, 'ax', 'Åland Islands', 'Åland eilanden', NULL, 1, 0),
(18, 'az', 'Azerbaijan', 'Azerbeidzjan', NULL, 1, 0),
(19, 'ba', 'Bosnia and Herzegovina', 'Bosnië-Herzegovina', NULL, 1, 0),
(20, 'bb', 'Barbados', 'Barbados', NULL, 1, 0),
(21, 'bd', 'Bangladesh', 'Bangladesh', NULL, 1, 0),
(22, 'be', 'Belgium', 'België', NULL, 1, 0),
(23, 'bf', 'Burkina Faso', 'Burkina Faso', NULL, 1, 0),
(24, 'bg', 'Bulgaria', 'Bulgarije', NULL, 1, 0),
(25, 'bh', 'Bahrain', 'Bahrein', NULL, 1, 0),
(26, 'bi', 'Burundi', 'Burundi', NULL, 1, 0),
(27, 'bj', 'Benin', 'Benin', NULL, 1, 0),
(28, 'bl', 'Saint Barthelemy', 'Sint Barthélemy', NULL, 1, 0),
(29, 'bm', 'Bermuda', 'Bermuda', NULL, 1, 0),
(30, 'bn', 'Brunei Darussalam', 'Brunei', NULL, 1, 0),
(31, 'bo', 'Bolivia', 'Bolivia', NULL, 1, 0),
(32, 'bq', 'Bonaire, Saint Eustatius, Saba', 'Bonaire, Sint Eustatius, Saba - Caribisch Nederlan', NULL, 1, 0),
(33, 'br', 'Brazil', 'Brazilië', NULL, 1, 0),
(34, 'bs', 'Bahamas', 'Bahama''s', NULL, 1, 0),
(35, 'bt', 'Bhutan', 'Bhutan', NULL, 1, 0),
(36, 'bv', 'Bouvet Island', 'Bouvet (eiland)', NULL, 1, 0),
(37, 'bw', 'Botswana', 'Botswana', NULL, 1, 0),
(38, 'by', 'Belarus', 'Wit-Rusland', NULL, 1, 0),
(39, 'bz', 'Belize', 'Belize', NULL, 1, 0),
(40, 'ca', 'Canada', 'Canada', NULL, 1, 0),
(41, 'cc', 'Cocos (Keeling) Islands', 'Cocos (Keeling) eilanden', NULL, 1, 0),
(42, 'cd', 'The Democratic Republic of the Congo', 'Democratische Republiek Congo (Kinshasa)', NULL, 1, 0),
(43, 'cf', 'Central African Republic', 'Centraal-Afrikaanse Republiek', NULL, 1, 0),
(44, 'cg', 'Republic of Congo', 'Congo (Brazzaville)', NULL, 1, 0),
(45, 'ch', 'Switzerland', 'Zwitserland', NULL, 1, 0),
(46, 'ci', 'Côte d''Ivoire', 'Ivoorkust', NULL, 1, 0),
(47, 'ck', 'Cook Islands', 'Cookeilanden', NULL, 1, 0),
(48, 'cl', 'Chile', 'Chili', NULL, 1, 0),
(49, 'cm', 'Cameroon', 'Kameroen', NULL, 1, 0),
(50, 'cn', 'China', 'China (Zhongguo)', NULL, 1, 0),
(51, 'co', 'Colombia', 'Colombia', NULL, 1, 0),
(52, 'cr', 'Costa Rica', 'Costa Rica', NULL, 1, 0),
(53, 'cu', 'Cuba', 'Cuba', NULL, 1, 0),
(54, 'cv', 'Cape Verde', 'Kaapverdië', NULL, 1, 0),
(55, 'cw', 'Curacao', 'Curação', NULL, 1, 0),
(56, 'cx', 'Christmas Island', 'Christmaseiland', NULL, 1, 0),
(57, 'cy', 'Cyprus', 'Cyprus', NULL, 1, 0),
(58, 'cz', 'Czech Republic', 'Tsjechië', NULL, 1, 0),
(59, 'de', 'Germany', 'Duitsland', NULL, 1, 0),
(60, 'dj', 'Djibouti', 'Djibouti', NULL, 1, 0),
(61, 'dk', 'Denmark', 'Denemarken', NULL, 1, 0),
(62, 'dm', 'Dominica', 'Dominica', NULL, 1, 0),
(63, 'do', 'Dominican Republic', 'Dominicaanse Republiek', NULL, 1, 0),
(64, 'dz', 'Algeria', 'Algerije', NULL, 1, 0),
(65, 'ec', 'Ecuador', 'Ecuador', NULL, 1, 0),
(66, 'ee', 'Estonia', 'Estland (Eesti)', NULL, 1, 0),
(67, 'eg', 'Egypt', 'Egypte', NULL, 1, 0),
(68, 'eh', 'Western Sahara', 'West-Sahara', NULL, 1, 0),
(69, 'er', 'Eritrea', 'Eritrea', NULL, 1, 0),
(70, 'es', 'Spain', 'Spanje', NULL, 1, 0),
(71, 'et', 'Ethiopia', 'Ethiopië', NULL, 1, 0),
(72, 'eu', 'European Union', 'Europese Unie', NULL, 1, 0),
(73, 'fi', 'Finland', 'Finland (Suomi Finland)', NULL, 1, 0),
(74, 'fj', 'Fiji', 'Fiji', NULL, 1, 0),
(75, 'fk', 'Falkland Islands (Islas Malvinas)', 'Falklandeilanden', NULL, 1, 0),
(76, 'fm', 'Federated States of Micronesia', 'Micronesië', NULL, 1, 0),
(77, 'fo', 'Faroe Islands', 'Faeröer', NULL, 1, 0),
(78, 'fr', 'France', 'Frankrijk', NULL, 1, 0),
(79, 'ga', 'Gabon', 'Gabon', NULL, 1, 0),
(80, 'gb', 'Great Britain', 'Groot-Brittannië', NULL, 1, 0),
(81, 'gd', 'Grenada', 'Grenada', NULL, 1, 0),
(82, 'ge', 'Georgia', 'Georgië', NULL, 1, 0),
(83, 'gf', 'French Guiana', 'Frans-Guyana', NULL, 1, 0),
(84, 'gg', 'Guernsey', 'Guernsey', NULL, 1, 0),
(85, 'gh', 'Ghana', 'Ghana', NULL, 1, 0),
(86, 'gi', 'Gibraltar', 'Gibraltar', NULL, 1, 0),
(87, 'gl', 'Greenland', 'Groenland (Kalaallit Nunaat)', NULL, 1, 0),
(88, 'gm', 'Gambia', 'Gambia', NULL, 1, 0),
(89, 'gn', 'Guinea', 'Republiek Guinee', NULL, 1, 0),
(90, 'gp', 'Guadeloupe', 'Guadeloupe', NULL, 1, 0),
(91, 'gq', 'Equatorial Guinea', 'Equatoriaal-Guinea', NULL, 1, 0),
(92, 'gr', 'Greece', 'Griekenland (Ellas, Hellas)', NULL, 1, 0),
(93, 'gs', 'South Georgia and the South Sandwich Islands', 'Zuid-Georgia en de Zuidelijke Sandwicheilanden', NULL, 1, 0),
(94, 'gt', 'Guatemala', 'Guatemala', NULL, 1, 0),
(95, 'gu', 'Guam', 'Guam', NULL, 1, 0),
(96, 'gw', 'Guinea-Bissau', 'Guinee-Bissau', NULL, 1, 0),
(97, 'gy', 'Guyana', 'Guyana', NULL, 1, 0),
(98, 'hk', 'Hong Kong', 'Hongkong (Xianggang)', NULL, 1, 0),
(99, 'hm', 'Heard Island and McDonald Islands', 'Heard en McDonald (eilanden)', NULL, 1, 0),
(100, 'hn', 'Honduras', 'Honduras', NULL, 1, 0),
(101, 'hr', 'Croatia (Hrvatska)', 'Kroatië', NULL, 1, 0),
(102, 'ht', 'Haiti', 'Haïti', NULL, 1, 0),
(103, 'hu', 'Hungary', 'Hongarije (Magyar)', NULL, 1, 0),
(104, 'id', 'Indonesia', 'Indonesië', NULL, 1, 0),
(105, 'ie', 'Ireland', 'Ierland (Eire)', NULL, 1, 0),
(106, 'il', 'Israel', 'Israël', NULL, 1, 0),
(107, 'im', 'Isle of Man', 'Man (Ellan Vannin) (eiland)', NULL, 1, 0),
(108, 'in', 'India', 'India', NULL, 1, 0),
(109, 'io', 'British Indian Ocean Territory', 'Britse gebiedsdelen in de Indische Oceaan: Chagos ', NULL, 1, 0),
(110, 'iq', 'Iraq', 'Irak', NULL, 1, 0),
(111, 'ir', 'Islamic Republic of Iran', 'Iran', NULL, 1, 0),
(112, 'is', 'Iceland', 'IJsland', NULL, 1, 0),
(113, 'it', 'Italy', 'Italië', NULL, 1, 0),
(114, 'je', 'Jersey', 'Jersey', NULL, 1, 0),
(115, 'jm', 'Jamaica', 'Jamaica', NULL, 1, 0),
(116, 'jo', 'Jordan', 'Jordanië', NULL, 1, 0),
(117, 'jp', 'Japan', 'Japan (Nippon)', NULL, 1, 0),
(118, 'ke', 'Kenya', 'Kenya', NULL, 1, 0),
(119, 'kg', 'Kyrgyzstan', 'Kirgizië', NULL, 1, 0),
(120, 'kh', 'Cambodia', 'Cambodja', NULL, 1, 0),
(121, 'ki', 'Kiribati', 'Kiribati', NULL, 1, 0),
(122, 'km', 'Comoros', 'Comoren', NULL, 1, 0),
(123, 'kn', 'Saint Kitts and Nevis', 'Sint Kitts en Nevis', NULL, 1, 0),
(124, 'kp', 'Democratic People''s Republic Korea', 'Noord Korea', NULL, 1, 0),
(125, 'kr', 'Republic of Korea', 'Zuid Korea', NULL, 1, 0),
(126, 'kw', 'Kuwait', 'Koeweit', NULL, 1, 0),
(127, 'ky', 'Cayman Islands', 'Caymaneilanden', NULL, 1, 0),
(128, 'kz', 'Kazakhstan', 'Kazachstan', NULL, 1, 0),
(129, 'la', 'Lao People''s Democratic Republic', 'Laos', NULL, 1, 0),
(130, 'lb', 'Lebanon', 'Libanon', NULL, 1, 0),
(131, 'lc', 'Saint Lucia', 'Sint Lucia', NULL, 1, 0),
(132, 'li', 'Liechtenstein', 'Liechtenstein', NULL, 1, 0),
(133, 'lk', 'Sri Lanka', 'Sri Lanka', NULL, 1, 0),
(134, 'lr', 'Liberia', 'Liberia', NULL, 1, 0),
(135, 'ls', 'Lesotho', 'Lesotho', NULL, 1, 0),
(136, 'lt', 'Lithuania', 'Litouwen (Lietuva)', NULL, 1, 0),
(137, 'lu', 'Luxembourg', 'Luxemburg (Lëtzebuerg)', NULL, 1, 0),
(138, 'lv', 'Latvia', 'Letland', NULL, 1, 0),
(139, 'ly', 'Libyan Arab Jamahiriya', 'Libië', NULL, 1, 0),
(140, 'ma', 'Morocco', 'Marokko', NULL, 1, 0),
(141, 'mc', 'Monaco', 'Monaco', NULL, 1, 0),
(142, 'md', 'Republic of Moldova', 'Moldavië', NULL, 1, 0),
(143, 'me', 'Montenegro', 'Montenegro', NULL, 1, 0),
(144, 'mf', 'Saint Martin', 'Sint Maarten', NULL, 1, 0),
(145, 'mg', 'Madagascar', 'Madagaskar', NULL, 1, 0),
(146, 'mh', 'Marshall Islands', 'Marshalleilanden', NULL, 1, 0),
(147, 'mk', 'The former Yugoslav Republic of Macedonia', 'Macedonië', NULL, 1, 0),
(148, 'ml', 'Mali', 'Mali', NULL, 1, 0),
(149, 'mm', 'Myanmar', 'Myanmar', NULL, 1, 0),
(150, 'mn', 'Mongolia', 'Mongolië', NULL, 1, 0),
(151, 'mo', 'Macao', 'Macau (Aomen)', NULL, 1, 0),
(152, 'mp', 'Northern Mariana Islands', 'Noordelijke Marianen-eilanden', NULL, 1, 0),
(153, 'mq', 'Martinique', 'Martinique', NULL, 1, 0),
(154, 'mr', 'Mauritania', 'Mauritanië', NULL, 1, 0),
(155, 'ms', 'Montserrat', 'Montserrat', NULL, 1, 0),
(156, 'mt', 'Malta', 'Malta', NULL, 1, 0),
(157, 'mu', 'Mauritius', 'Mauritius', NULL, 1, 0),
(158, 'mv', 'Maldives', 'Maldiven', NULL, 1, 0),
(159, 'mw', 'Malawi', 'Malawi', NULL, 1, 0),
(160, 'mx', 'Mexico', 'Mexico', NULL, 1, 0),
(161, 'my', 'Malaysia', 'Maleisië', NULL, 1, 0),
(162, 'mz', 'Mozambique', 'Mozambique', NULL, 1, 0),
(163, 'na', 'Namibia', 'Namibië', NULL, 1, 0),
(164, 'nc', 'New Caledonia', 'Nieuw-Caledonië', NULL, 1, 0),
(165, 'ne', 'Niger', 'Niger', NULL, 1, 0),
(166, 'nf', 'Norfolk Island', 'Norfolk (eiland)', NULL, 1, 0),
(167, 'ng', 'Nigeria', 'Nigeria', NULL, 1, 0),
(168, 'ni', 'Nicaragua', 'Nicaragua', NULL, 1, 0),
(169, 'nl', 'Netherlands', 'Nederland', NULL, 1, 0),
(170, 'no', 'Norway', 'Noorwegen (Norge)', NULL, 1, 0),
(171, 'np', 'Nepal', 'Nepal', NULL, 1, 0),
(172, 'nr', 'Nauru', 'Nauru', NULL, 1, 0),
(173, 'nu', 'Niue', 'Niue', NULL, 1, 0),
(174, 'nz', 'New Zealand', 'Nieuw-Zeeland', NULL, 1, 0),
(175, 'om', 'Oman', 'Oman', NULL, 1, 0),
(176, 'pa', 'Panama', 'Panama', NULL, 1, 0),
(177, 'pe', 'Peru', 'Peru', NULL, 1, 0),
(178, 'pf', 'French Polynesia', 'Frans Polynesië (Tahiti)', NULL, 1, 0),
(179, 'pg', 'Papua New Guinea', 'Papua Nieuw-Guinea', NULL, 1, 0),
(180, 'ph', 'Philippines', 'Filipijnen', NULL, 1, 0),
(181, 'pk', 'Pakistan', 'Pakistan', NULL, 1, 0),
(182, 'pl', 'Poland', 'Polen (Polska)', NULL, 1, 0),
(183, 'pm', 'Saint Pierre and Miquelon', 'Sint Pierre en Miquelon', NULL, 1, 0),
(184, 'pn', 'Pitcairn Island', 'Pitcairn (eiland)', NULL, 1, 0),
(185, 'pr', 'Puerto Rico', 'Puerto Rico', NULL, 1, 0),
(186, 'ps', 'Palestinian territory, occupied', 'Palestijnse bezette gebieden', NULL, 1, 0),
(187, 'pt', 'Portugal', 'Portugal', NULL, 1, 0),
(188, 'pw', 'Palau', 'Palau (Belau)', NULL, 1, 0),
(189, 'py', 'Paraguay', 'Paraguay', NULL, 1, 0),
(190, 'qa', 'Qatar', 'Qatar', NULL, 1, 0),
(191, 're', 'Reunion Island', 'Réunion (eiland)', NULL, 1, 0),
(192, 'ro', 'Romania', 'Roemenië (Rumania, Roumania)', NULL, 1, 0),
(193, 'rs', 'Serbia', 'Servië', NULL, 1, 0),
(194, 'ru', 'Russian Federation', 'Rusland', NULL, 1, 0),
(195, 'rw', 'Rwanda', 'Rwanda', NULL, 1, 0),
(196, 'sa', 'Saudi Arabia', 'Saoedi-Arabië', NULL, 1, 0),
(197, 'sb', 'Solomon Islands', 'Salomonseilanden', NULL, 1, 0),
(198, 'sc', 'Seychelles', 'Seychellen', NULL, 1, 0),
(199, 'sd', 'Sudan', 'Soedan', NULL, 1, 0),
(200, 'se', 'Sweden', 'Zweden (Sverige)', NULL, 1, 0),
(201, 'sg', 'Singapore', 'Singapore', NULL, 1, 0),
(202, 'sh', 'Saint Helena', 'Sint Helena', NULL, 1, 0),
(203, 'si', 'Slovenia', 'Slovenië', NULL, 1, 0),
(204, 'sj', 'Svalbard and Jan Mayen Islands', 'Svalbard en Jan Mayen (eilanden)', NULL, 1, 0),
(205, 'sk', 'Slovak Republic', 'Slowakije', NULL, 1, 0),
(206, 'sl', 'Sierra Leone', 'Sierra Leone', NULL, 1, 0),
(207, 'sm', 'San Marino', 'San Marino', NULL, 1, 0),
(208, 'sn', 'Senegal', 'Senegal', NULL, 1, 0),
(209, 'so', 'Somalia', 'Somalië', NULL, 1, 0),
(210, 'sr', 'Suriname', 'Suriname', NULL, 1, 0),
(211, 'ss', 'South Sudan', 'Zuid Soedan', NULL, 1, 0),
(212, 'st', 'Sao Tome and Principe', 'Sao Tomé en Principe (eilanden)', NULL, 1, 0),
(213, 'sv', 'El Salvador', 'El Salvador', NULL, 1, 0),
(214, 'sx', 'Saint Martin', 'Sint Maarten', NULL, 1, 0),
(215, 'sy', 'Syrian Arab Republic', 'Syrië', NULL, 1, 0),
(216, 'sz', 'Swaziland', 'Swaziland', NULL, 1, 0),
(217, 'tc', 'Turks and Caicos Islands', 'Turks- en Caicoseilanden', NULL, 1, 0),
(218, 'td', 'Chad', 'Tsjaad', NULL, 1, 0),
(219, 'tf', 'French Southern Territories', 'Franse Zuidelijke gebieden (Terres Australes et An', NULL, 1, 0),
(220, 'tg', 'Togo', 'Togo', NULL, 1, 0),
(221, 'th', 'Thailand', 'Thailand', NULL, 1, 0),
(222, 'tj', 'Tajikistan', 'Tadzjikistan', NULL, 1, 0),
(223, 'tk', 'Tokelau', 'Tokelau', NULL, 1, 0),
(224, 'tl', 'Timor-Leste', 'Oost-Timor', NULL, 1, 0),
(225, 'tm', 'Turkmenistan', 'Turkmenistan', NULL, 1, 0),
(226, 'tn', 'Tunisia', 'Tunesië', NULL, 1, 0),
(227, 'to', 'Tonga', 'Tonga', NULL, 1, 0),
(228, 'tp', 'East Timor', 'Oost-Timor (Timor Lorosae, Timor Leste)', NULL, 1, 0),
(229, 'tr', 'Turkey', 'Turkije', NULL, 1, 0),
(230, 'tt', 'Trinidad and Tobago', 'Trinidad en Tobago', NULL, 1, 0),
(231, 'tv', 'Tuvalu', 'Tuvalu', NULL, 1, 0),
(232, 'tw', 'Taiwan (Province of China)', 'Taiwan', NULL, 1, 0),
(233, 'tz', 'Tanzania', 'Tanzania', NULL, 1, 0),
(234, 'ua', 'Ukraine', 'Oekraïne', NULL, 1, 0),
(235, 'ug', 'Uganda', 'Oeganda', NULL, 1, 0),
(236, 'uk', 'United Kingdom', 'Verenigd Koninkrijk (Groot-Brittannië)', NULL, 1, 0),
(237, 'um', 'United States Minor Outlying Islands', 'Verenigde Staten: kleine eilanden', NULL, 1, 0),
(238, 'us', 'United States', 'Verenigde Staten', NULL, 1, 0),
(239, 'uy', 'Uruguay', 'Uruguay', NULL, 1, 0),
(240, 'uz', 'Uzbekistan', 'Oezbekistan', NULL, 1, 0),
(241, 'va', 'Vatican City State', 'Vaticaanstad (Holy See, Santa Sede)', NULL, 1, 0),
(242, 'vc', 'Saint Vincent and the Grenadines', 'Sint Vincent en de Grenadinen', NULL, 1, 0),
(243, 've', 'Venezuela', 'Venezuela', NULL, 1, 0),
(244, 'vg', 'Virgin Islands (British)', 'Britse Maagdeneilanden', NULL, 1, 0),
(245, 'vi', 'Virgin Islands (US)', 'Amerikaanse Maagdeneilanden', NULL, 1, 0),
(246, 'vn', 'Viet Nam', 'Vietnam', NULL, 1, 0),
(247, 'vu', 'Vanuatu', 'Vanuatu', NULL, 1, 0),
(248, 'wf', 'Wallis and Futuna Islands', 'Wallis en Futuna (eilanden)', NULL, 1, 0),
(249, 'ws', 'Samoa', 'Samoa', NULL, 1, 0),
(250, 'ye', 'Yemen', 'Jemen', NULL, 1, 0),
(251, 'yt', 'Mayotte', 'Mayotte', NULL, 1, 0),
(252, 'za', 'South Africa', 'Zuid-Afrika', NULL, 1, 0),
(253, 'zm', 'Zambia', 'Zambia', NULL, 1, 0),
(254, 'zw', 'Zimbabwe', 'Zimbabwe', NULL, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `dates`
--

CREATE TABLE IF NOT EXISTS `dates` (
  `date_id` bigint(20) NOT NULL auto_increment,
  `event_id` bigint(20) NOT NULL,
  `year_code` varchar(20) collate utf8_unicode_ci NOT NULL,
  `start_date` date default NULL,
  `end_date` date default NULL,
  `date_as_text` varchar(30) collate utf8_unicode_ci NOT NULL,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL,
  `long_description` text collate utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`date_id`),
  KEY `dates_event_id_idx` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=13 ;

--
-- Dumping data for table `dates`
--

INSERT INTO `dates` (`date_id`, `event_id`, `year_code`, `start_date`, `end_date`, `date_as_text`, `description`, `long_description`, `enabled`, `deleted`) VALUES
(1, 1, '2012', '2012-04-11', '2012-04-14', '11 April - 14 April 2012', '9th European Social Science History Conference', '9th European Social Science History Conference\r\nGlasgow, Scotland, UK\r\nWednesday 11 - Saturday 14 April 2012', 1, 0),
(2, 1, '2010', '2010-04-13', '2010-04-16', '13 April - 16 April 2010', '8th European Social Science History Conference', '8th European Social Science History Conference\r\nGhent, Belgium\r\nApril 2010', 1, 0),
(3, 1, '2008', '2008-02-26', '2008-03-01', '26 February - 1 March 2008', '7th European Social Science History Conference', '7th European Social Science History Conference\r\nLisbon, Portugal\r\nMarch 2008', 1, 0),
(4, 1, '2006', '2006-03-22', '2006-03-25', '22 - 25 March 2006', 'Sixth European Social Science History Conference', '', 1, 0),
(5, 1, '2004', '2004-03-24', '2004-03-27', '24 - 27 March 2004', 'Fifth European Social Science History Conference', '', 1, 0),
(6, 1, '2002', '2002-02-27', '2002-03-02', '27 February - 2 March 2002', 'Fourth European Social Science History Conference', '', 1, 0),
(7, 1, '2000', '2000-04-12', '2000-04-15', '12 - 15 April 2000', 'Third European Social Science History Conference', '', 1, 0),
(8, 1, '1998', '1998-03-05', '1998-03-07', '5 - 7 March 1998', 'Second European Social Science History Conference', '', 1, 0),
(9, 1, '1996', '1996-05-09', '1996-05-11', '9 - 11 May 1996', 'First European Social Science History Conference', '', 1, 0),
(10, 3, '2014', NULL, NULL, 'yyy-zzz 2014', 'First NEHA Conference', '', 1, 0),
(11, 2, 'januari 2012', '2012-01-26', '2012-01-26', '26 januari 2012', 'IISG Vriendendag 26 januari 2012', '', 1, 0),
(12, 2, 'juni 2011', '2011-06-23', '2011-06-23', '23 juni 2011', 'IISG Vriendendag 23 juni 2011', '', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `days`
--

CREATE TABLE IF NOT EXISTS `days` (
  `day_id` bigint(20) NOT NULL auto_increment,
  `day` date NOT NULL,
  `date_id` bigint(20) NOT NULL,
  `day_number` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`day_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=5 ;

--
-- Dumping data for table `days`
--

INSERT INTO `days` (`day_id`, `day`, `date_id`, `day_number`, `enabled`, `deleted`) VALUES
(1, '2012-04-11', 1, 1, 1, 0),
(2, '2012-04-12', 1, 2, 1, 0),
(3, '2012-04-13', 1, 3, 1, 0),
(4, '2012-04-14', 1, 4, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `dynamic_pages`
--

CREATE TABLE IF NOT EXISTS `dynamic_pages` (
  `dynamic_page_id` bigint(20) NOT NULL auto_increment,
  `content` longtext collate utf8_unicode_ci NOT NULL,
  `cache` longtext collate utf8_unicode_ci,
  `page_id` bigint(20) NOT NULL,
  `date_id` bigint(20) default NULL,
  PRIMARY KEY  (`dynamic_page_id`),
  KEY `date_id` (`date_id`),
  KEY `page_id` (`page_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=28 ;

--
-- Dumping data for table `dynamic_pages`
--

INSERT INTO `dynamic_pages` (`dynamic_page_id`, `content`, `cache`, `page_id`, `date_id`) VALUES
(1, '<overview domain="Event" id="url">\r\n	<column name="id" />\r\n	<column name="code" />\r\n	<column name="shortName" />\r\n	<column name="longName" />\r\n	<column name="enabled" />\r\n	<column name="dates" />    \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 1, NULL),
(2, '<form domain="Event" id="url">\r\n	<column name="id" />\r\n	<column name="code" />\r\n	<column name="shortName" />\r\n	<column name="longName" />\r\n	<column name="enabled" />\r\n	<column name="deleted" /> \r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 2, NULL),
(3, '<overview domain="EventDate" id="url">\r\n	<column name="event" />\r\n	<column name="yearCode" />\r\n	<column name="startDate" />\r\n	<column name="endDate" />\r\n	<column name="days" />\r\n	<column name="dateAsText" />\r\n	<column name="description" />  \r\n	<column name="longDescription" />\r\n</overview>\r\n\r\n<buttons>\r\n        <button type="back" />\r\n        <button action="edit" />\r\n</buttons>', NULL, 3, NULL),
(4, '<form domain="EventDate" id="url">\r\n	<column name="event" readonly="true" />\r\n	<column name="yearCode" />\r\n	<column name="startDate" />\r\n	<column name="endDate" />\r\n	<column name="days" multiple="true">\r\n		<column name="dayNumber" />\r\n		<column name="day" />	\r\n	</column>\r\n	<column name="dateAsText" />\r\n	<column name="description" />  \r\n	<column name="longDescription" />\r\n	<column name="enabled" />\r\n        <column name="deleted" />\r\n \r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 4, NULL),
(5, '<form domain="Event">\r\n	<column name="code" />\r\n	<column name="shortName" />\r\n	<column name="longName" />\r\n	<column name="enabled" />\r\n	\r\n	<column name="dates">\r\n		<column name="yearCode" />\r\n		<column name="startDate" />\r\n		<column name="endDate" />\r\n		<column name="dateAsText" />\r\n		<column name="description" />  \r\n		<column name="longDescription" />\r\n		<column name="enabled" />\r\n	</column>\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 5, NULL),
(6, '<form domain="EventDate">\r\n	<column name="event" id="url" />\r\n	<column name="yearCode" />\r\n	<column name="startDate" />\r\n	<column name="endDate" />\r\n	<column name="days" multiple="true">\r\n		<column name="dayNumber" />\r\n		<column name="day" />	\r\n	</column>\r\n	<column name="dateAsText" />\r\n	<column name="description" />  \r\n	<column name="longDescription" />\r\n	<column name="enabled" />\r\n \r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 6, NULL),
(7, '<form domain="Network">\r\n	<column name="name" />\r\n	<column name="comment" />\r\n	<column name="url" />\r\n	<column name="showOnline" />\r\n	<column name="showInternal" />	\r\n	<column name="chairs" multiple="true">\r\n		<column name="chair" />\r\n		<column name="isMainChair" />\r\n	</column>\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 7, NULL),
(8, '<form domain="Title">\r\n	<column name="title" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 8, NULL),
(9, '<overview domain="Equipment" id="url">\r\n	<column name="id" />\r\n	<column name="equipment" />\r\n	<column name="description" />\r\n	<column name="imageUrl" />\r\n	<column name="enabled" />    \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 19, NULL),
(10, '<form domain="Equipment" id="url">\r\n	<column name="id" />\r\n	<column name="equipment" />\r\n	<column name="description" />\r\n	<column name="imageUrl" />\r\n	<column name="enabled" />\r\n	<column name="deleted" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />    \r\n</form>', NULL, 17, NULL),
(11, '<form domain="Equipment">\r\n	<column name="equipment" />\r\n	<column name="description" />\r\n	<column name="imageUrl" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />	\r\n</form>', NULL, 15, NULL),
(12, '<form domain="Network" id="url">\r\n	<column name="name" />\r\n	<column name="comment" />\r\n	<column name="url" />\r\n	<column name="showOnline" />\r\n	<column name="showInternal" />	\r\n	<column name="chairs" multiple="true">\r\n		<column name="chair" />\r\n		<column name="isMainChair" />\r\n	</column>\r\n	<column name="enabled" />\r\n	<column name="deleted" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 10, NULL),
(13, '<overview domain="Network" id="url">\r\n	<column name="name" />\r\n	<column name="comment" />\r\n	<column name="url" />\r\n	<column name="showOnline" />\r\n	<column name="showInternal" />	\r\n	<column name="chairs" />\r\n	<column name="enabled" />\r\n	<column name="deleted" />\r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 11, NULL),
(14, '<form domain="Title" id="url">\r\n   <column name="title" />\r\n   <column name="enabled" />\r\n\r\n   <button type="cancel" />\r\n   <button type="save" />  \r\n</form>', NULL, 12, NULL),
(15, '<overview domain="Title" id="url">\r\n	<column name="title" />\r\n	<column name="enabled" />\r\n	<column name="deleted" />   \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 13, NULL),
(16, '<overview domain="Room" id="url">\r\n	<column name="roomName" />\r\n	<column name="roomNumber" />\r\n	<column name="noOfSeats" />\r\n	<column name="comment" />\r\n	<column name="deleted" />   \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>\r\n', NULL, 18, NULL),
(17, '<form domain="Room" id="url">\r\n	<column name="roomName" />\r\n	<column name="roomNumber" />\r\n	<column name="noOfSeats" />\r\n	<column name="comment" />\r\n	<column name="enabled" />\r\n	<column name="deleted" /> \r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 16, NULL),
(18, '<form domain="Room">\r\n	<column name="roomName" />\r\n	<column name="roomNumber" />\r\n	<column name="noOfSeats" />\r\n	<column name="comment" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 14, NULL),
(19, '<table domain="Title">\r\n   <column name="title" />\r\n   <column name="enabled" />\r\n   <column name="deleted" />\r\n</table>\r\n\r\n<buttons>\r\n   <button type="back" />\r\n   <button action="create" />\r\n</buttons>', NULL, 20, NULL),
(20, '<table domain="Room">\r\n	<column name="roomName" />\r\n	<column name="roomNumber" />\r\n	<column name="noOfSeats" />  \r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>\r\n', NULL, 21, NULL),
(21, '<form domain="Network" id="url">\r\n	<column name="name" />\r\n	<column name="comment" />\r\n	<column name="url" />\r\n	<column name="showOnline" />\r\n	<column name="showInternal" />	\r\n	<column name="chairs" multiple="true">\r\n		<column name="chair" />\r\n		<column name="isMainChair" />\r\n	</column>\r\n	<column name="enabled" />\r\n	<column name="disabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 22, NULL),
(22, '<table domain="Equipment">\r\n	<column name="equipment" />\r\n	<column name="description" />\r\n	<column name="imageUrl" />\r\n	<column name="enabled" />  \r\n        <column name="deleted" />  \r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>\r\n', NULL, 23, NULL),
(23, '<table domain="Event">\r\n	<column name="code" />\r\n	<column name="shortName" />\r\n	\r\n	<column name="dates">\r\n		<column name="yearCode" />\r\n	</column>\r\n</table>', NULL, 24, NULL),
(24, '<table domain="Session" index="true">\r\n	<column name="code" />\r\n	<column name="name" /> \r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>', NULL, 26, NULL),
(25, '<form domain="Session" id="url">\r\n	<column name="id" />\r\n	<column name="name" />\r\n	<column name="code" />\r\n	<column name="comment" />\r\n	<column name="enabled" />\r\n	<column name="deleted" /> \r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 27, NULL),
(26, '<form domain="Session">\r\n	<column name="name" />\r\n	<column name="code" />\r\n	<column name="comment" />\r\n	<column name="enabled" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 28, NULL),
(27, '<overview domain="Session" id="url">\r\n	<column name="id" />\r\n	<column name="name" />\r\n	<column name="code" />\r\n	<column name="comment" />\r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 29, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `equipment`
--

CREATE TABLE IF NOT EXISTS `equipment` (
  `equipment_id` bigint(20) NOT NULL auto_increment,
  `date_id` bigint(20) default NULL,
  `equipment` varchar(30) collate utf8_unicode_ci NOT NULL,
  `description` text collate utf8_unicode_ci,
  `image_url` varchar(50) collate utf8_unicode_ci default NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`equipment_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=5 ;

--
-- Dumping data for table `equipment`
--

INSERT INTO `equipment` (`equipment_id`, `date_id`, `equipment`, `description`, `image_url`, `enabled`, `deleted`) VALUES
(1, 1, 'Slide Projector', NULL, NULL, 1, 0),
(2, 1, 'Beamer', NULL, NULL, 1, 0),
(3, 1, 'Video recorder', NULL, NULL, 1, 0),
(4, 1, 'DVD player', NULL, NULL, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE IF NOT EXISTS `events` (
  `event_id` bigint(20) NOT NULL auto_increment,
  `code` varchar(20) collate utf8_unicode_ci NOT NULL,
  `short_name` varchar(20) collate utf8_unicode_ci NOT NULL,
  `long_name` varchar(50) collate utf8_unicode_ci NOT NULL,
  `type` varchar(20) collate utf8_unicode_ci default NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`event_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=4 ;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`event_id`, `code`, `short_name`, `long_name`, `type`, `enabled`, `deleted`) VALUES
(1, 'esshc', 'ESSHC', 'European Social Science History Conference', NULL, 1, 0),
(2, 'iisgvriendendag', 'IISG Vriendendag', 'IISG Vriendendag', NULL, 1, 0),
(3, 'neha', 'NEHA', 'Nederlandsch Economisch-Historisch Archief', NULL, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `fee_amounts`
--

CREATE TABLE IF NOT EXISTS `fee_amounts` (
  `fee_amount_id` bigint(20) NOT NULL auto_increment,
  `date_id` bigint(20) default NULL,
  `fee_state_id` bigint(20) NOT NULL,
  `end_date` date NOT NULL,
  `nr_of_days_start` int(11) NOT NULL,
  `nr_of_days_end` int(11) NOT NULL,
  `fee_amount` decimal(10,2) NOT NULL default '0.00',
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`fee_amount_id`),
  KEY `date_id` (`date_id`),
  KEY `fee_state_id` (`fee_state_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=15 ;

--
-- Dumping data for table `fee_amounts`
--

INSERT INTO `fee_amounts` (`fee_amount_id`, `date_id`, `fee_state_id`, `end_date`, `nr_of_days_start`, `nr_of_days_end`, `fee_amount`, `enabled`, `deleted`) VALUES
(1, 1, 1, '2011-12-31', 1, 1, 100.00, 1, 0),
(2, 1, 1, '2011-12-31', 2, 4, 200.00, 1, 0),
(5, 1, 1, '2012-04-30', 1, 1, 125.00, 1, 0),
(6, 1, 1, '2012-04-30', 2, 4, 250.00, 1, 0),
(9, 1, 2, '2012-04-30', 1, 4, 0.00, 1, 0),
(10, 1, 3, '2012-04-30', 1, 4, 0.00, 1, 0),
(11, 1, 4, '2012-04-30', 1, 4, 90.00, 1, 0),
(12, 1, 5, '2012-04-30', 1, 4, 0.00, 1, 0),
(13, 1, 8, '2012-04-30', 1, 1, 150.00, 1, 0),
(14, 1, 8, '2012-04-30', 2, 4, 300.00, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `fee_states`
--

CREATE TABLE IF NOT EXISTS `fee_states` (
  `fee_state_id` bigint(20) NOT NULL auto_increment,
  `event_id` bigint(20) default NULL,
  `name` varchar(50) collate utf8_unicode_ci NOT NULL,
  `is_default_fee` tinyint(1) NOT NULL default '0',
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`fee_state_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=9 ;

--
-- Dumping data for table `fee_states`
--

INSERT INTO `fee_states` (`fee_state_id`, `event_id`, `name`, `is_default_fee`, `enabled`, `deleted`) VALUES
(1, 1, 'Normal Fee', 1, 1, 0),
(2, 1, 'No Fee', 0, 1, 0),
(3, 1, 'No Fee and Beurs', 0, 1, 0),
(4, 1, 'Student Fee', 0, 1, 0),
(5, 1, 'IISG Fee', 0, 1, 0),
(6, 1, 'Bijbetaling', 0, 1, 0),
(7, 1, 'Book exhibit', 0, 1, 0),
(8, 1, 'On Site', 0, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE IF NOT EXISTS `groups` (
  `group_id` bigint(20) NOT NULL auto_increment,
  `name` varchar(30) collate utf8_unicode_ci NOT NULL,
  `date_id` bigint(20) default NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`group_id`),
  KEY `groups_date_id_idx` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

--
-- Dumping data for table `groups`
--


-- --------------------------------------------------------

--
-- Table structure for table `groups_pages`
--

CREATE TABLE IF NOT EXISTS `groups_pages` (
  `group_id` bigint(20) NOT NULL,
  `page_id` bigint(20) NOT NULL,
  `show_in_menu` tinyint(1) NOT NULL default '1',
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`group_id`,`page_id`),
  KEY `groups_pages_page_id_fk` (`page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `groups_pages`
--


-- --------------------------------------------------------

--
-- Table structure for table `networks`
--

CREATE TABLE IF NOT EXISTS `networks` (
  `network_id` bigint(20) NOT NULL auto_increment,
  `date_id` bigint(20) default NULL,
  `name` varchar(30) collate utf8_unicode_ci NOT NULL,
  `comment` text collate utf8_unicode_ci,
  `url` varchar(255) collate utf8_unicode_ci NOT NULL,
  `show_online` tinyint(1) NOT NULL default '1',
  `show_internal` tinyint(1) NOT NULL default '1',
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`network_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=3 ;

--
-- Dumping data for table `networks`
--

INSERT INTO `networks` (`network_id`, `date_id`, `name`, `comment`, `url`, `show_online`, `show_internal`, `enabled`, `deleted`) VALUES
(1, NULL, 'Nieuw netwerk', NULL, 'URL', 1, 1, 1, 0),
(2, NULL, 'netwerk', NULL, 'url', 1, 1, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `networks_chairs`
--

CREATE TABLE IF NOT EXISTS `networks_chairs` (
  `network_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `is_main_chair` tinyint(1) NOT NULL default '0',
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`network_id`,`user_id`),
  KEY `network_id` (`network_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `networks_chairs`
--

INSERT INTO `networks_chairs` (`network_id`, `user_id`, `is_main_chair`, `enabled`, `deleted`) VALUES
(1, 1, 0, 1, 0),
(1, 2, 0, 1, 0),
(1, 3, 0, 1, 0),
(2, 1, 1, 1, 0),
(2, 2, 0, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `pages`
--

CREATE TABLE IF NOT EXISTS `pages` (
  `page_id` bigint(20) NOT NULL auto_increment,
  `title_code` varchar(50) collate utf8_unicode_ci default NULL,
  `title_arg` varchar(50) collate utf8_unicode_ci default NULL,
  `title_default` varchar(50) collate utf8_unicode_ci NOT NULL,
  `controller` varchar(20) collate utf8_unicode_ci NOT NULL,
  `action` varchar(20) collate utf8_unicode_ci NOT NULL,
  `description` text collate utf8_unicode_ci,
  `parent_page_id` bigint(20) default NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`page_id`),
  KEY `pages_parent_page_id_idx` (`parent_page_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=30 ;

--
-- Dumping data for table `pages`
--

INSERT INTO `pages` (`page_id`, `title_code`, `title_arg`, `title_default`, `controller`, `action`, `description`, `parent_page_id`, `enabled`, `deleted`) VALUES
(1, 'default.overview.label', 'event.label', 'Event overview', 'event', 'show', NULL, NULL, 1, 0),
(2, 'default.edit.label', 'event.label', 'Edit event', 'event', 'edit', NULL, NULL, 1, 0),
(3, 'default.overview.label', 'eventdate.label', 'Event date overview', 'eventDate', 'show', NULL, NULL, 1, 0),
(4, 'default.edit.label', 'eventdate.label', 'Edit event date', 'eventDate', 'edit', NULL, NULL, 1, 0),
(5, 'default.create.label', 'event.label', 'Create event', 'event', 'create', NULL, NULL, 1, 0),
(6, 'default.create.label', 'eventdate.label', 'Create event date', 'eventDate', 'create', NULL, NULL, 1, 0),
(7, 'default.create.label', 'network.label', 'Create network', 'network', 'create', NULL, NULL, 1, 0),
(8, 'default.create.label', 'title.label', 'Create title', 'title', 'create', NULL, NULL, 1, 0),
(9, 'event.multiple.label', NULL, 'Events', 'event', 'list', NULL, NULL, 1, 0),
(10, 'default.edit.label', 'network.label', 'Edit network', 'network', 'edit', NULL, NULL, 1, 0),
(11, 'default.overview.label', 'network.label', 'Network overview', 'network', 'show', NULL, NULL, 1, 0),
(12, 'default.edit.label', 'title.label', 'Edit title', 'title', 'edit', NULL, NULL, 1, 0),
(13, 'default.overview.label', 'title.label', 'Title overview', 'title', 'show', NULL, NULL, 1, 0),
(14, 'default.create.label', 'room.label', 'Create room', 'room', 'create', NULL, NULL, 1, 0),
(15, 'default.create.label', 'equipment.label', 'Create equipment', 'equipment', 'create', NULL, NULL, 1, 0),
(16, 'default.edit.label', 'room.label', 'Edit room', 'room', 'edit', NULL, NULL, 1, 0),
(17, 'default.edit.label', 'equipment.label', 'Edit equipment', 'equipment', 'edit', NULL, NULL, 1, 0),
(18, 'default.overview.label', 'room.label', 'Room overview', 'room', 'show', NULL, NULL, 1, 0),
(19, 'default.overview.label', 'equipment.label', 'Equipment overview', 'equipment', 'show', NULL, NULL, 1, 0),
(20, 'default.list.label', 'title.label', 'Title list', 'title', 'list', NULL, NULL, 1, 0),
(21, 'default.list.label', 'room.label', 'Room list', 'room', 'list', NULL, NULL, 1, 0),
(22, 'default.list.label', 'network.label', 'Network list', 'network', 'list', NULL, NULL, 1, 0),
(23, 'default.list.label', 'equipment.label', 'Equipment list', 'equipment', 'list', NULL, NULL, 1, 0),
(24, 'default.list.label', 'event.label', '', 'event', 'list_all', NULL, NULL, 1, 0),
(25, 'default.welcome.label', NULL, 'Welcome', 'event', 'index', NULL, NULL, 1, 0),
(26, 'default.list.label', 'session.label', 'Session list', 'session', 'list', NULL, NULL, 1, 0),
(27, 'default.edit.label', 'session.label', 'Edit session', 'session', 'edit', NULL, NULL, 1, 0),
(28, 'default.create.label', 'session.label', 'Create session', 'session', 'create', NULL, NULL, 1, 0),
(29, 'default.overview.label', 'session.label', 'Session overview', 'session', 'show', NULL, NULL, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` bigint(20) NOT NULL auto_increment,
  `role` varchar(20) collate utf8_unicode_ci NOT NULL,
  `description` text collate utf8_unicode_ci,
  `full_rights` tinyint(1) NOT NULL,
  PRIMARY KEY  (`role_id`),
  UNIQUE KEY `role` (`role`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=5 ;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`role_id`, `role`, `description`, `full_rights`) VALUES
(1, 'superAdmin', NULL, 1),
(2, 'admin', NULL, 0),
(3, 'user', NULL, 0),
(4, 'participant', NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `rooms`
--

CREATE TABLE IF NOT EXISTS `rooms` (
  `room_id` bigint(20) NOT NULL auto_increment,
  `date_id` bigint(20) default NULL,
  `room_name` varchar(30) collate utf8_unicode_ci NOT NULL,
  `room_number` varchar(10) collate utf8_unicode_ci NOT NULL,
  `number_of_seets` int(11) NOT NULL,
  `comment` text collate utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`room_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=29 ;

--
-- Dumping data for table `rooms`
--

INSERT INTO `rooms` (`room_id`, `date_id`, `room_name`, `room_number`, `number_of_seets`, `comment`, `enabled`, `deleted`) VALUES
(1, 1, 'A', 'A', 999, NULL, 1, 0),
(4, 1, 'B', 'B', 999, NULL, 1, 0),
(5, 1, 'C', 'C', 999, NULL, 1, 0),
(6, 1, 'D', 'D', 999, NULL, 1, 0),
(7, 1, 'E', 'E', 999, NULL, 1, 0),
(8, 1, 'F', 'F', 999, NULL, 1, 0),
(9, 1, 'G', 'G', 999, NULL, 1, 0),
(10, 1, 'H', 'H', 999, NULL, 1, 0),
(11, 1, 'I', 'I', 999, NULL, 1, 0),
(12, 1, 'J', 'J', 999, NULL, 1, 0),
(13, 1, 'K', 'K', 999, NULL, 1, 0),
(14, 1, 'L', 'L', 999, NULL, 1, 0),
(15, 1, 'M', 'M', 999, NULL, 1, 0),
(16, 1, 'N', 'N', 999, NULL, 1, 0),
(17, 1, 'O', 'O', 999, NULL, 1, 0),
(18, 1, 'P', 'P', 999, NULL, 1, 0),
(19, 1, 'Q', 'Q', 999, NULL, 1, 0),
(20, 1, 'R', 'R', 999, NULL, 1, 0),
(21, 1, 'S', 'S', 999, NULL, 1, 0),
(22, 1, 'T', 'T', 999, NULL, 1, 0),
(23, 1, 'U', 'U', 999, NULL, 1, 0),
(24, 1, 'V', 'V', 999, NULL, 1, 0),
(25, 1, 'W', 'W', 999, NULL, 1, 0),
(26, 1, 'X', 'X', 999, NULL, 1, 0),
(27, 1, 'Y', 'Y', 999, NULL, 1, 0),
(28, 1, 'Z', 'Z', 999, NULL, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `room_sessiondatetime_equipment`
--

CREATE TABLE IF NOT EXISTS `room_sessiondatetime_equipment` (
  `room_id` bigint(20) NOT NULL,
  `session_datetime_id` bigint(20) NOT NULL,
  `equipment_id` bigint(20) NOT NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`room_id`,`session_datetime_id`,`equipment_id`),
  KEY `room_id` (`room_id`),
  KEY `session_datetime_id` (`session_datetime_id`),
  KEY `equipment_id` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `room_sessiondatetime_equipment`
--


-- --------------------------------------------------------

--
-- Table structure for table `sessions`
--

CREATE TABLE IF NOT EXISTS `sessions` (
  `session_id` bigint(20) NOT NULL auto_increment,
  `date_id` bigint(20) default NULL,
  `session_code` varchar(10) collate utf8_unicode_ci NOT NULL,
  `session_name` varchar(255) collate utf8_unicode_ci NOT NULL,
  `session_comment` text collate utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`session_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=5 ;

--
-- Dumping data for table `sessions`
--

INSERT INTO `sessions` (`session_id`, `date_id`, `session_code`, `session_name`, `session_comment`, `enabled`, `deleted`) VALUES
(1, 1, 'AFR02', 'Knowledge, Culture and Empowerment', NULL, 1, 0),
(2, 1, 'ANT02', 'Urban Labour in Roman Italy', NULL, 1, 0),
(3, 1, 'ANT03', 'The Social Institution of Money in the Ancient World', NULL, 1, 0),
(4, 1, 'ANT04', 'Social Networks Analysis and the Ancient Economy: Networks Around Commodities', NULL, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `session_datetime`
--

CREATE TABLE IF NOT EXISTS `session_datetime` (
  `session_datetime_id` bigint(20) NOT NULL auto_increment,
  `day_id` bigint(20) NOT NULL,
  `index_number` int(11) NOT NULL,
  `period` varchar(30) collate utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`session_datetime_id`),
  KEY `day_id` (`day_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=18 ;

--
-- Dumping data for table `session_datetime`
--

INSERT INTO `session_datetime` (`session_datetime_id`, `day_id`, `index_number`, `period`, `enabled`, `deleted`) VALUES
(1, 1, 1, '8.30 - 10.30', 1, 0),
(3, 1, 2, '11.00 - 13.00', 1, 0),
(4, 1, 3, '14.00 - 16.00', 1, 0),
(5, 1, 4, '16.30 -18.30', 1, 0),
(6, 2, 5, '8.30 - 10.30', 1, 0),
(7, 2, 6, '11.00 - 13.00', 1, 0),
(8, 2, 7, '14.00 - 16.00', 1, 0),
(9, 2, 8, '16.00 - 18.30', 1, 0),
(10, 3, 9, '8.30 - 10.30', 1, 0),
(11, 3, 10, '11.00 - 13.00', 1, 0),
(12, 3, 11, '14.00 - 16.00', 1, 0),
(13, 3, 12, '16.30 - 18.30', 1, 0),
(14, 4, 13, '8.30 - 10.30', 1, 0),
(15, 4, 14, '11.00 - 13.00', 1, 0),
(16, 4, 15, '14.00 - 16.00', 1, 0),
(17, 4, 16, '16.30 - 18.30', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `session_users`
--

CREATE TABLE IF NOT EXISTS `session_users` (
  `session_user_id` bigint(20) NOT NULL auto_increment,
  `user_id` bigint(20) NOT NULL,
  `session_id` bigint(20) NOT NULL,
  `chair` tinyint(1) NOT NULL default '0',
  `organiser` tinyint(1) NOT NULL default '0',
  `author` tinyint(1) NOT NULL default '0',
  `coauthor` tinyint(1) NOT NULL default '0',
  `discussant` tinyint(1) NOT NULL default '0',
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`session_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

--
-- Dumping data for table `session_users`
--


-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

CREATE TABLE IF NOT EXISTS `settings` (
  `setting_id` bigint(20) NOT NULL auto_increment,
  `property` varchar(50) collate utf8_unicode_ci NOT NULL,
  `value` varchar(255) collate utf8_unicode_ci NOT NULL,
  `event_id` bigint(20) default NULL,
  PRIMARY KEY  (`setting_id`),
  KEY `settings_event_id_idx` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=2 ;

--
-- Dumping data for table `settings`
--

INSERT INTO `settings` (`setting_id`, `property`, `value`, `event_id`) VALUES
(1, 'lastUpdated', 'april 2012', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `titles`
--

CREATE TABLE IF NOT EXISTS `titles` (
  `title_id` bigint(20) NOT NULL auto_increment,
  `event_id` bigint(20) default NULL,
  `title` varchar(10) collate utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`title_id`),
  KEY `titles_event_id_idx` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=8 ;

--
-- Dumping data for table `titles`
--

INSERT INTO `titles` (`title_id`, `event_id`, `title`, `enabled`, `deleted`) VALUES
(1, 1, 'Mr.', 1, 0),
(2, 1, 'Ms.', 1, 0),
(3, 1, 'Drs.', 1, 0),
(4, 1, 'Dr.', 1, 0),
(5, 1, 'Prof. Dr.', 1, 0),
(6, 1, 'Prof.', 1, 0),
(7, 1, 'Mrs.', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint(20) NOT NULL auto_increment,
  `email` varchar(30) collate utf8_unicode_ci NOT NULL,
  `full_name` varchar(30) collate utf8_unicode_ci NOT NULL,
  `institute` varchar(50) collate utf8_unicode_ci NOT NULL,
  `country_id` bigint(20) NOT NULL,
  `language` varchar(10) collate utf8_unicode_ci NOT NULL,
  `password` varchar(128) collate utf8_unicode_ci NOT NULL,
  `salt` varchar(26) collate utf8_unicode_ci default NULL,
  `enabled` tinyint(1) NOT NULL default '1',
  `deleted` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `users_country_id_idx` (`country_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=4 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `email`, `full_name`, `institute`, `country_id`, `language`, `password`, `salt`, `enabled`, `deleted`) VALUES
(1, 'em@em.com', 'Lastname, Firstname', 'International Institute for Social History', 169, 'nl', '230e337572084fc40fdc869f53deadeff591861428a2ed1b48f267565c3c1f58f41b49aa671cc9d2e5de9a35b7285a8786192a0da646cd48d9d0c1be4e7a5819', 'l806hw0aJp6PcXKh3aelytHM0C', 1, 0),
(2, 'email@email.com', 'Abc, Def', 'International Institute for Social History', 1, 'nl', 'pw', NULL, 1, 0),
(3, 'mail@mail.com', 'Qwe, Rty', 'Institute', 1, 'en', 'pw', NULL, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `users_groups`
--

CREATE TABLE IF NOT EXISTS `users_groups` (
  `user_id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`user_id`,`group_id`),
  KEY `users_groups_group_id_fk` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `users_groups`
--


-- --------------------------------------------------------

--
-- Table structure for table `users_pages`
--

CREATE TABLE IF NOT EXISTS `users_pages` (
  `user_id` bigint(20) NOT NULL,
  `page_id` bigint(20) NOT NULL,
  `denied` tinyint(1) NOT NULL default '0',
  `show_in_menu` tinyint(1) NOT NULL default '1',
  `date_id` bigint(20) default NULL,
  PRIMARY KEY  (`user_id`,`page_id`),
  KEY `users_pages_date_id_idx` (`date_id`),
  KEY `users_pages_page_id_idx` (`page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `users_pages`
--


-- --------------------------------------------------------

--
-- Table structure for table `users_roles`
--

CREATE TABLE IF NOT EXISTS `users_roles` (
  `user_role_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `date_id` bigint(20) default NULL,
  PRIMARY KEY  (`user_role_id`),
  KEY `users_roles_user_id_idx` (`user_id`),
  KEY `users_roles_role_id_idx` (`role_id`),
  KEY `users_roles_date_id_idx` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `users_roles`
--

INSERT INTO `users_roles` (`user_role_id`, `user_id`, `role_id`, `date_id`) VALUES
(0, 1, 1, NULL);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin_pages`
--
ALTER TABLE `admin_pages`
  ADD CONSTRAINT `admin_pages_event_id_fk` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`),
  ADD CONSTRAINT `admin_pages_page_id_fk` FOREIGN KEY (`page_id`) REFERENCES `pages` (`page_id`);

--
-- Constraints for table `dates`
--
ALTER TABLE `dates`
  ADD CONSTRAINT `dates_event_id_fk` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

--
-- Constraints for table `days`
--
ALTER TABLE `days`
  ADD CONSTRAINT `days_date_id_fk` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

--
-- Constraints for table `dynamic_pages`
--
ALTER TABLE `dynamic_pages`
  ADD CONSTRAINT `dynamic_pages_date_id_fk` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`),
  ADD CONSTRAINT `dynamic_pages_page_id_fk` FOREIGN KEY (`page_id`) REFERENCES `pages` (`page_id`);

--
-- Constraints for table `equipment`
--
ALTER TABLE `equipment`
  ADD CONSTRAINT `equipment_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

--
-- Constraints for table `fee_amounts`
--
ALTER TABLE `fee_amounts`
  ADD CONSTRAINT `fee_amounts_ibfk_2` FOREIGN KEY (`fee_state_id`) REFERENCES `fee_states` (`fee_state_id`),
  ADD CONSTRAINT `fee_amounts_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

--
-- Constraints for table `fee_states`
--
ALTER TABLE `fee_states`
  ADD CONSTRAINT `fee_states_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

--
-- Constraints for table `groups`
--
ALTER TABLE `groups`
  ADD CONSTRAINT `groups_date_id_fk` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

--
-- Constraints for table `groups_pages`
--
ALTER TABLE `groups_pages`
  ADD CONSTRAINT `groups_pages_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`),
  ADD CONSTRAINT `groups_pages_page_id_fk` FOREIGN KEY (`page_id`) REFERENCES `pages` (`page_id`);

--
-- Constraints for table `networks`
--
ALTER TABLE `networks`
  ADD CONSTRAINT `networks_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

--
-- Constraints for table `networks_chairs`
--
ALTER TABLE `networks_chairs`
  ADD CONSTRAINT `networks_chairs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `networks_chairs_ibfk_3` FOREIGN KEY (`network_id`) REFERENCES `networks` (`network_id`);

--
-- Constraints for table `pages`
--
ALTER TABLE `pages`
  ADD CONSTRAINT `pages_parent_page_id_fk` FOREIGN KEY (`parent_page_id`) REFERENCES `pages` (`page_id`);

--
-- Constraints for table `rooms`
--
ALTER TABLE `rooms`
  ADD CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

--
-- Constraints for table `room_sessiondatetime_equipment`
--
ALTER TABLE `room_sessiondatetime_equipment`
  ADD CONSTRAINT `room_sessiondatetime_equipment_ibfk_3` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`),
  ADD CONSTRAINT `room_sessiondatetime_equipment_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
  ADD CONSTRAINT `room_sessiondatetime_equipment_ibfk_2` FOREIGN KEY (`session_datetime_id`) REFERENCES `session_datetime` (`session_datetime_id`);

--
-- Constraints for table `sessions`
--
ALTER TABLE `sessions`
  ADD CONSTRAINT `sessions_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

--
-- Constraints for table `session_datetime`
--
ALTER TABLE `session_datetime`
  ADD CONSTRAINT `session_datetime_ibfk_1` FOREIGN KEY (`day_id`) REFERENCES `days` (`day_id`);

--
-- Constraints for table `settings`
--
ALTER TABLE `settings`
  ADD CONSTRAINT `settings_event_id_fk` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

--
-- Constraints for table `titles`
--
ALTER TABLE `titles`
  ADD CONSTRAINT `titles_event_id_fk` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_country_id_fk` FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`);

--
-- Constraints for table `users_groups`
--
ALTER TABLE `users_groups`
  ADD CONSTRAINT `users_groups_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`),
  ADD CONSTRAINT `users_groups_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `users_pages`
--
ALTER TABLE `users_pages`
  ADD CONSTRAINT `users_pages_date_id_fk` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`),
  ADD CONSTRAINT `users_pages_page_id_fk` FOREIGN KEY (`page_id`) REFERENCES `pages` (`page_id`),
  ADD CONSTRAINT `users_pages_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `users_roles`
--
ALTER TABLE `users_roles`
  ADD CONSTRAINT `users_roles_date_id_fk` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`),
  ADD CONSTRAINT `users_roles_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
  ADD CONSTRAINT `users_roles_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
