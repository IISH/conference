SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


CREATE TABLE IF NOT EXISTS `admin_pages` (
  `admin_page_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `page_id` bigint(20) NOT NULL,
  `event_id` bigint(20) DEFAULT NULL,
  `show_in_menu` tinyint(1) NOT NULL DEFAULT '1',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`admin_page_id`),
  KEY `admin_pages_page_id_idx` (`page_id`),
  KEY `admin_pages_event_id_idx` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `countries` (
  `country_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tld` varchar(2) COLLATE utf8_unicode_ci NOT NULL,
  `name_english` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `name_dutch` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `remarks` text COLLATE utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`country_id`),
  UNIQUE KEY `tld` (`tld`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=511 ;

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
(254, 'zw', 'Zimbabwe', 'Zimbabwe', NULL, 1, 0),
(256, '**', 'Not found', 'Niet gevonden', NULL, 1, 0);

CREATE TABLE IF NOT EXISTS `dates` (
  `date_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) NOT NULL,
  `year_code` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `date_as_text` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `long_description` text COLLATE utf8_unicode_ci,
  `create_statistics` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`date_id`),
  KEY `dates_event_id_idx` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `days` (
  `day_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `day` date NOT NULL,
  `date_id` bigint(20) NOT NULL,
  `day_number` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `original_daysautonr` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`day_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `dynamic_pages` (
  `dynamic_page_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext COLLATE utf8_unicode_ci NOT NULL,
  `cache` longtext COLLATE utf8_unicode_ci,
  `page_id` bigint(20) NOT NULL,
  `date_id` bigint(20) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`dynamic_page_id`),
  KEY `date_id` (`date_id`),
  KEY `page_id` (`page_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=77 ;

INSERT INTO `dynamic_pages` (`dynamic_page_id`, `content`, `cache`, `page_id`, `date_id`, `enabled`, `deleted`) VALUES
(1, '<overview domain="Event" id="url">\r\n	<column name="id" />\r\n	<column name="code" />\r\n	<column name="shortName" />\r\n	<column name="longName" />\r\n	<column name="enabled" />\r\n	<column name="dates" />    \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 1, NULL, 1, 0),
(2, '<form domain="Event" id="url">\r\n	<column name="id" />\r\n	<column name="code" readonly="true" />\r\n	<column name="shortName" />\r\n	<column name="longName" />\r\n	<column name="enabled" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 2, NULL, 1, 0),
(3, '<overview domain="EventDate" id="url">\r\n	<column name="event" />\r\n	<column name="yearCode" />\r\n	<column name="startDate" />\r\n	<column name="endDate" />\r\n	<column name="days" />\r\n	<column name="dateAsText" />\r\n	<column name="description" />  \r\n	<column name="longDescription" />\r\n	<column name="createStatistics" />\r\n</overview>\r\n\r\n<buttons>\r\n        <button type="back" />\r\n	<button action="create" />\r\n        <button action="edit" />\r\n</buttons>', NULL, 3, NULL, 1, 0),
(5, '<form domain="Event">\r\n	<column name="code" />\r\n	<column name="shortName" />\r\n	<column name="longName" />\r\n	<column name="enabled" />\r\n	\r\n	<column name="dates">\r\n		<column name="yearCode" />\r\n		<column name="startDate" />\r\n		<column name="endDate" />\r\n		<column name="dateAsText" />\r\n		<column name="description" />  \r\n		<column name="longDescription" />\r\n		<column name="enabled" />\r\n	</column>\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 5, NULL, 1, 0),
(7, '<form domain="Network">\r\n	<column name="name" />\r\n	<column name="comment" />\r\n	<column name="url" />\r\n	<column name="showOnline" />\r\n	<column name="sessions" />\r\n	<column name="chairs" multiple="true">\r\n		<column name="chair" />\r\n		<column name="isMainChair" />\r\n	</column>\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 7, NULL, 1, 0),
(8, '<form domain="Title">\r\n	<column name="title" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 8, NULL, 1, 0),
(9, '<overview domain="Equipment" id="url">\r\n	<column name="id" />\r\n	<column name="code" />\r\n	<column name="equipment" />\r\n	<column name="description" />\r\n	<column name="imageUrl" />\r\n	<column name="enabled" />    \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 19, NULL, 1, 0),
(10, '<form domain="Equipment" id="url">\r\n	<column name="id" />\r\n	<column name="code" />\r\n	<column name="equipment" />\r\n	<column name="description" />\r\n	<column name="imageUrl" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="delete" />\r\n	<button type="save" />    \r\n</form>', NULL, 17, NULL, 1, 0),
(11, '<form domain="Equipment">\r\n	<column name="code" />\r\n	<column name="equipment" />\r\n	<column name="description" />\r\n	<column name="imageUrl" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />	\r\n</form>', NULL, 15, NULL, 1, 0),
(14, '<form domain="Title" id="url">\r\n   <column name="title" />\r\n   <column name="enabled" />\r\n\r\n   <button type="cancel" />\r\n   <button type="delete" />\r\n   <button type="save" />  \r\n</form>', NULL, 12, NULL, 1, 0),
(15, '<overview domain="Title" id="url">\r\n	<column name="id" />\r\n	<column name="title" />\r\n	<column name="enabled" />\r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 13, NULL, 1, 0),
(19, '<table domain="Title" index="true">\r\n   <column name="title" />\r\n</table>\r\n\r\n<buttons>\r\n   <button type="back" />\r\n   <button action="create" />\r\n</buttons>', NULL, 20, NULL, 1, 0),
(20, '<table domain="Room" index="true">\r\n	<column name="roomNumber" />\r\n	<column name="roomName" />\r\n	<column name="noOfSeats" />  \r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>\r\n', NULL, 21, NULL, 1, 0),
(21, '<table domain="Network" index="true">\r\n	<column name="name" />\r\n	<column name="showOnline" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>\r\n', NULL, 22, NULL, 1, 0),
(22, '<table domain="Equipment" index="true">	\r\n	<column name="code" />\r\n	<column name="equipment" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>\r\n', NULL, 23, NULL, 1, 0),
(24, '<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>\r\n\r\n<table domain="Session" index="true">\r\n	<column name="code" /> \r\n	<column name="name" /> \r\n	<column name="id" hidden="true" id="true" />\r\n	<column name="state" interactive="session-state-select-tbl" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>\r\n', NULL, 26, NULL, 1, 0),
(26, '<form domain="Session">\r\n	<column name="name" />\r\n	<column name="code" />\r\n	<column name="abstr" />\r\n	<column name="comment" />\r\n	<column name="state" />\r\n	<column name="enabled" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 28, NULL, 1, 0),
(28, '<form domain="ParticipantState">\r\n	<column name="state" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />    \r\n</form>', NULL, 33, NULL, 1, 0),
(29, '<form domain="ParticipantState" id="url">\r\n	<column name="id" />\r\n	<column name="state" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="delete" />	\r\n	<button type="save" />    \r\n</form>', NULL, 34, NULL, 1, 0),
(30, '<overview domain="ParticipantState" id="url">\r\n	<column name="id" />\r\n	<column name="state" />\r\n	<column name="enabled" />\r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 35, NULL, 1, 0),
(31, '<table domain="ParticipantState" index="true">\r\n	<column name="state" /> \r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>', NULL, 36, NULL, 1, 0),
(34, '<table domain="EmailTemplate" index="true">\r\n	<column name="description" />\r\n	<column name="showInBackend" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>', NULL, 42, NULL, 1, 0),
(35, '<overview domain="EmailTemplate" id="url">\r\n	<column name="id" />\r\n	<column name="description" />\r\n	<column name="subject" />\r\n	<column name="body" />\r\n	<column name="sender" />\r\n	<column name="comment" />\r\n	<column name="enabled" />\r\n</overview>\r\n\r\n<buttons>\r\n        <button type="back" />\r\n        <button action="edit" />\r\n</buttons>', NULL, 43, NULL, 1, 0),
(36, '<form domain="EmailCode">\r\n	<column name="code" />\r\n	<column name="description" />\r\n	<column name="groovyScript" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 44, NULL, 1, 0),
(37, '<table domain="EmailCode" index="true">\r\n	<column name="code" />\r\n	<column name="description" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>\r\n\r\n\r\n', NULL, 46, NULL, 1, 0),
(38, '<form domain="EmailCode" id="url">\r\n	<column name="id" />\r\n	<column name="code" />\r\n	<column name="description" />\r\n	<column name="groovyScript" />\r\n	<column name="enabled" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="delete" />\r\n	<button type="save" />  \r\n</form>', NULL, 45, NULL, 1, 0),
(39, '<overview domain="EmailCode" id="url">\r\n	<column name="id" />\r\n	<column name="code" />\r\n	<column name="description" />\r\n	<column name="groovyScript" />\r\n	<column name="enabled" />\r\n</overview>\r\n\r\n<buttons>\r\n        <button type="back" />\r\n        <button action="edit" />\r\n</buttons>', NULL, 47, NULL, 1, 0),
(40, '<table domain="Setting" action="edit" index="true">\r\n	<column name="property" />\r\n	<column name="value" />\r\n	<column name="event" />\r\n	<column name="showInBackend" hidden="true" eq="1" /> \r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n</buttons>', NULL, 48, NULL, 1, 0),
(41, '<form domain="Setting" id="url">\r\n	<column name="property" />\r\n	<column name="value" />\r\n	<column name="showInBackend" hidden="true" eq="1" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 49, NULL, 1, 0),
(42, '<form domain="RequestMap">\r\n	<column name="url" />\r\n	<column name="configAttribute" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 50, NULL, 1, 0),
(43, '<form domain="RequestMap" id="url">\r\n	<column name="url" />\r\n	<column name="configAttribute" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 51, NULL, 1, 0),
(44, '<table domain="RequestMap" index="true">\r\n	<column name="url" />\r\n	<column name="configAttribute" />\r\n</table>\r\n\r\n<buttons>\r\n   <button type="back" />\r\n   <button action="create" />\r\n</buttons>', NULL, 52, NULL, 1, 0),
(45, '<overview domain="RequestMap" id="url">\r\n	<column name="url" />\r\n	<column name="configAttribute" /> \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 53, NULL, 1, 0),
(46, '<table domain="Extra" index="true">\r\n	<column name="extra" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>', NULL, 54, NULL, 1, 0),
(48, '<table domain="ParticipantDate" index="true" totals="true">\r\n	<column name="user">\r\n                <column name="id" id="true" hidden="true" />\r\n		<column name="lastName" />\r\n		<column name="firstName" />\r\n		<column name="country" />\r\n	</column>\r\n	<column name="state" />\r\n	<column name="invitationLetter" eq="1" hidden="true" />\r\n	<column name="invitationLetterSent" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n</buttons>', NULL, 55, NULL, 1, 0),
(49, '<table domain="ParticipantDate" index="true" totals="true">\r\n	<column name="user">\r\n                <column name="id" id="true" hidden="true" />\r\n		<column name="lastName" />\r\n		<column name="firstName" />\r\n	</column>\r\n	<column name="feeState" />\r\n	<column name="lowerFeeRequested" eq="1" hidden="true" />\r\n	<column name="lowerFeeAnswered" />  \r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n</buttons>', NULL, 56, NULL, 1, 0),
(50, '<table domain="User">\r\n	<column name="id" />\r\n	<column name="lastName" />\r\n	<column name="firstName" />\r\n	<column name="email" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>', NULL, 65, NULL, 1, 0),
(51, '<table domain="Group" index="true">	\r\n	<column name="name" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>', NULL, 61, NULL, 1, 0),
(55, '<form domain="Extra">\r\n	<column name="extra" />\r\n	<column name="enabled" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 71, NULL, 1, 0),
(56, '<form domain="Extra" id="url">\r\n	<column name="id" />\r\n	<column name="extra" />\r\n	<column name="enabled" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="delete" />\r\n	<button type="save" />  \r\n</form>', NULL, 72, NULL, 1, 0),
(57, '<overview domain="Extra" id="url">\r\n	<column name="id" />\r\n	<column name="extra" />\r\n	<column name="enabled" /> \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 73, NULL, 1, 0),
(58, '<table domain="PaperState" index="true">\r\n	<column name="description" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="create" />\r\n</buttons>', NULL, 74, NULL, 1, 0),
(59, '<form domain="PaperState">\r\n	<column name="description" />\r\n	<column name="enabled" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 75, NULL, 1, 0),
(60, '<form domain="PaperState" id="url">\r\n	<column name="description" />\r\n	<column name="enabled" />\r\n	\r\n	<button type="cancel" />\r\n	<button type="delete" />\r\n	<button type="save" />  \r\n</form>', NULL, 76, NULL, 1, 0),
(61, '<overview domain="PaperState" id="url">\r\n	<column name="description" />\r\n	<column name="enabled" />\r\n</overview>\r\n\r\n<buttons>	\r\n	<button type="back" />\r\n	<button action="edit" />  \r\n</buttons>', NULL, 77, NULL, 1, 0),
(62, '<table domain="ParticipantType" index="true">\r\n   <column name="type" />\r\n   <column name="withPaper" />\r\n</table>\r\n\r\n<buttons>\r\n   <button type="back" />\r\n   <button action="create" />\r\n</buttons>', NULL, 78, NULL, 1, 0),
(63, '<form domain="ParticipantType">\r\n	<column name="type" />\r\n	<column name="withPaper" />\r\n	<column name="importance" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 79, NULL, 1, 0),
(64, '<form domain="ParticipantType" id="url">\r\n	<column name="type" />\r\n	<column name="withPaper" />\r\n	<column name="importance" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="delete" />\r\n	<button type="save" />    \r\n</form>', NULL, 80, NULL, 1, 0),
(65, '<overview domain="ParticipantType" id="url">\r\n	<column name="type" />\r\n	<column name="withPaper" />\r\n	<column name="importance" />\r\n	<column name="enabled" /> \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 81, NULL, 1, 0),
(66, '<table domain="SessionDateTime" index="true">\r\n	<column name="day">\r\n		<column name="dayNumber" />\r\n		<column name="day" />\r\n	</column>\r\n	<column name="indexNumber" />\r\n	<column name="period" />\r\n</table>\r\n\r\n<buttons>\r\n   <button type="back" />\r\n   <button action="create" />\r\n</buttons>', NULL, 82, NULL, 1, 0),
(67, '<form domain="SessionDateTime">\r\n	<column name="day" />\r\n	<column name="indexNumber" />\r\n	<column name="period" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 83, NULL, 1, 0),
(68, '<form domain="SessionDateTime" id="url">\r\n	<column name="day" />\r\n	<column name="indexNumber" />\r\n	<column name="period" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="delete" />\r\n	<button type="save" />    \r\n</form>', NULL, 84, NULL, 1, 0),
(69, '<overview domain="SessionDateTime" id="url">\r\n	<column name="day">\r\n		<column name="dayNumber" />\r\n		<column name="day" />\r\n	</column>\r\n	<column name="indexNumber" />\r\n	<column name="period" />\r\n	<column name="enabled" /> \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n</buttons>', NULL, 85, NULL, 1, 0),
(70, '<table domain="Volunteering" index="true">\r\n	<column name="description" />\r\n</table>\r\n\r\n<buttons>\r\n   <button type="back" />\r\n   <button action="create" />\r\n</buttons>', NULL, 86, NULL, 1, 0),
(71, '<form domain="Volunteering">\r\n	<column name="description" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="save" />  \r\n</form>', NULL, 87, NULL, 1, 0),
(72, '<form domain="Volunteering" id="url">\r\n	<column name="description" />\r\n	<column name="enabled" />\r\n\r\n	<button type="cancel" />\r\n	<button type="delete" />\r\n	<button type="save" />    \r\n</form>', NULL, 88, NULL, 1, 0),
(73, '<overview domain="Volunteering" id="url">\r\n	<column name="description" />\r\n	<column name="enabled" /> \r\n</overview>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n	<button action="edit" />\r\n	<button type="delete" />\r\n</buttons>', NULL, 89, NULL, 1, 0),
(74, '<table domain="ParticipantDate" index="true">\r\n	<column name="user" >\r\n		<column name="id" id="true" hidden="true" />\r\n		<column name="lastName" />\r\n		<column name="firstName" />\r\n	</column>\r\n	<column name="extras" notEmpty="true" />\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n</buttons>', NULL, 90, NULL, 1, 0),
(75, '<table domain="Paper" index="true">\r\n<column name="networkProposal" filter="true" />\r\n<column name="user">\r\n<column name="id" id="true" hidden="true" />\r\n<column name="lastName" />\r\n<column name="firstName" />\r\n</column>\r\n\r\n<column name="id" hidden="true" />\r\n<column name="state" interactive="paper-state-select" />\r\n</table>\r\n\r\n<buttons>\r\n<button type="back" />\r\n</buttons>\r\n', NULL, 94, NULL, 1, 0),
(76, '<table domain="ParticipantDate" index="true">\r\n	<column name="user">\r\n		<column name="lastName" />\r\n		<column name="firstName" />\r\n		<column name="id" id="true" hidden="true" />\r\n		<column name="gender" eq="null" hideFilter="true" hideSorting="true" interactive="change-gender"  />\r\n	</column>\r\n</table>\r\n\r\n<buttons>\r\n	<button type="back" />\r\n</buttons>\r\n', NULL, 106, NULL, 1, 0);

CREATE TABLE IF NOT EXISTS `email_codes` (
  `email_code_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `code` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `groovy_script` text COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`email_code_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=7 ;

INSERT INTO `email_codes` (`email_code_id`, `event_id`, `code`, `description`, `groovy_script`, `enabled`, `deleted`) VALUES
(1, NULL, 'NameParticipant', 'Name of the participant', 'def result = sql.rows("SELECT title, firstname, lastname FROM users WHERE user_id = :userId", params)\r\n\r\nif (result?.size() == 1) {\r\n	def values = result[0].values()\r\n	values.removeAll([''null'', null])\r\n	values.join('' '')\r\n}\r\nelse {\r\n	''-''\r\n} ', 1, 0),
(2, NULL, 'EmailParticipant', 'Email address of the participant', 'def result = sql.rows("SELECT email FROM users WHERE user_id = :userId", params)\r\n\r\nif (result?.size() == 1) {\r\n	result[0][''email'']\r\n} \r\nelse {\r\n	''-''\r\n}', 1, 0),
(3, NULL, 'ParticipantData', 'Name, address, email, tel, organisation, department and papers of the participant', 'def personalData = sql.rows("SELECT user_id, title, firstname, lastname, organisation, department, email FROM users WHERE user_id = :userId", params)\r\n\r\ndef papers = sql.rows("""\r\n    SELECT n.name, p.session_proposal, p.title, p.abstract, p.co_authors, p.comment\r\n    FROM papers p\r\n    LEFT JOIN networks n\r\n    ON p.network_proposal_id = n.network_id\r\n    WHERE p.user_id = :userId\r\n    AND p.date_id = :dateId\r\n""", params)\r\n\r\nif (personalData?.size() == 1) {\r\n    def participant = personalData[0].subMap([''title'', ''firstname'', ''lastname'']).values()\r\n    participant.removeAll([''null'', null])\r\n\r\n    def text = """        \r\n        |Participant:\r\n        |Name:          ${participant.join('' '')}\r\n        |ID:            ${personalData[0][''user_id'']}  \r\n        |Organisation:  ${personalData[0][''organisation'']}     \r\n        |Department:    ${personalData[0][''department'']} \r\n        |Email:         ${personalData[0][''email'']}"""\r\n    \r\n    if (papers?.size() > 0) {\r\n        text += "\\n\\nPapers:"\r\n    } \r\n    \r\n    papers.each { paper -> \r\n        text += """\r\n            |Paper title:             ${paper[''title'']}\r\n            |Paper abstract:          ${paper[''abstract'']}\r\n            |Co-authors:              ${paper[''co_authors'']}\r\n            |Registered for network:  ${paper[''name'']}\r\n            |Session proposal:        ${paper[''session_proposal'']}            \r\n            """\r\n    }\r\n    \r\n    text.stripMargin().trim().replaceAll(''null'', ''-'')\r\n} \r\nelse {\r\n    ''-''\r\n}', 1, 0),
(4, NULL, 'PaperData', 'Participants paper titles and the equipment needed', 'def papers = sql.rows("""\r\n    SELECT p.title, e.equipment\r\n    FROM papers p\r\n    INNER JOIN paper_equipment AS pe\r\n    ON p.paper_id = pe.paper_id\r\n    INNER JOIN equipment AS e\r\n    ON pe.equipment_id = e.equipment_id\r\n    WHERE p.user_id = :userId\r\n    AND p.date_id = :dateId\r\n""", params)\r\n\r\nif (papers?.size() > 0) {\r\n    def curPaper = null\r\n    def equipment = []\r\n    def text = ""\r\n    \r\n    papers.each { paper -> \r\n        if (!curPaper) {\r\n            curPaper = paper\r\n        }\r\n    \r\n        if (curPaper[''title''] == paper[''title'']) {\r\n            equipment.add(paper[''equipment''])\r\n        }\r\n        else {            \r\n            text += """              \r\n                |Paper title:    ${curPaper[''title'']}\r\n                |Equipment:      ${equipment.join('', '')}\r\n            """\r\n            \r\n            equipment.clear()\r\n            curPaper = paper\r\n        }    \r\n    }\r\n    \r\n    text += """ \r\n        |Paper title:    ${curPaper[''title'']}\r\n        |Equipment:      ${equipment.join('', '')}   \r\n    """\r\n    \r\n    text.stripMargin().trim().replaceAll(''null'', ''-'')\r\n} \r\nelse {\r\n    "-"\r\n}', 1, 0),
(5, NULL, 'NetworkNames', 'The names of the participants networks', 'def networks = sql.rows("""\r\n    SELECT n.name\r\n    FROM papers p\r\n    LEFT JOIN networks n\r\n    ON p.network_proposal_id = n.network_id\r\n    WHERE p.user_id = :userId\r\n    AND p.date_id = :dateId\r\n""", params)\r\n\r\nif (networks?.size() > 0) {\r\n    networks.collect { it[''name''] }.join('', '').replaceAll(''null'', ''-'')\r\n} \r\nelse {\r\n    "-"\r\n}', 1, 0),
(6, NULL, 'CurrentDateTime', 'Shows the current date/time', 'import java.text.SimpleDateFormat\r\n\r\nSimpleDateFormat formatter = new SimpleDateFormat("d MMMMM yyyy HH:mm:ss")\r\nformatter.format(new Date())', 1, 0);

CREATE TABLE IF NOT EXISTS `email_templates` (
  `email_template_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `subject` varchar(78) COLLATE utf8_unicode_ci NOT NULL,
  `body` text COLLATE utf8_unicode_ci NOT NULL,
  `sender` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `action` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `query_type` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sort_order` int(11) NOT NULL DEFAULT '0',
  `show_in_backend` tinyint(1) NOT NULL DEFAULT '1',
  `comment` text COLLATE utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`email_template_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `equipment` (
  `equipment_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_id` bigint(20) DEFAULT NULL,
  `code` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `equipment` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci,
  `image_url` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`equipment_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `events` (
  `event_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `short_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `long_name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`event_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `extras` (
  `extra_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_id` bigint(20) NOT NULL,
  `extra` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`extra_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `fee_amounts` (
  `fee_amount_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_id` bigint(20) DEFAULT NULL,
  `fee_state_id` bigint(20) NOT NULL,
  `end_date` date NOT NULL,
  `nr_of_days_start` int(11) NOT NULL,
  `nr_of_days_end` int(11) NOT NULL,
  `fee_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`fee_amount_id`),
  KEY `date_id` (`date_id`),
  KEY `fee_state_id` (`fee_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `fee_states` (
  `fee_state_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `is_default_fee` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`fee_state_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `groups` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `event_id` bigint(20) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`group_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `groups_pages` (
  `group_id` bigint(20) NOT NULL,
  `page_id` bigint(20) NOT NULL,
  PRIMARY KEY (`group_id`,`page_id`),
  KEY `groups_pages_page_id_fk` (`page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `maintenance_queries` (
  `maintenance_query_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `query` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `order` int(11) NOT NULL DEFAULT '9000',
  `created_by` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`maintenance_query_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=6 ;

INSERT INTO `maintenance_queries` (`maintenance_query_id`, `query`, `description`, `order`, `created_by`, `enabled`, `deleted`) VALUES
(1, 'CALL setTimeStamp(0, ''START MAINTENANCE'');', '', 10, 'gcu', 1, 0),
(2, 'CALL dataMaintenance();', 'Do some data maintenance', 100, 'gcu', 1, 0),
(4, 'CALL calculateStatistics();', 'calculate statistics', 1000, 'gcu', 1, 0),
(5, 'CALL setTimeStamp(0, ''END MAINTENANCE'');', '', 9999, 'gcu', 1, 0);

CREATE TABLE IF NOT EXISTS `networks` (
  `network_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_id` bigint(20) DEFAULT NULL,
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `comment` text COLLATE utf8_unicode_ci,
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `show_online` tinyint(1) NOT NULL DEFAULT '1',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `original_id` tinyint(4) DEFAULT NULL,
  `original_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`network_id`),
  KEY `date_id_2` (`date_id`,`name`,`deleted`,`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `networks_chairs` (
  `network_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `is_main_chair` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`network_id`,`user_id`),
  KEY `network_id` (`network_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `pages` (
  `page_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title_code` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title_arg` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `title_default` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `controller` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `action` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `sort_order` int(11) NOT NULL DEFAULT '999',
  `show_in_menu` tinyint(1) NOT NULL DEFAULT '0',
  `description` text COLLATE utf8_unicode_ci,
  `parent_page_id` bigint(20) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `url_query` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`page_id`),
  KEY `pages_parent_page_id_idx` (`parent_page_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=114 ;

INSERT INTO `pages` (`page_id`, `title_code`, `title_arg`, `title_default`, `controller`, `action`, `sort_order`, `show_in_menu`, `description`, `parent_page_id`, `enabled`, `deleted`, `url_query`) VALUES
(1, 'event.label', NULL, 'Event overview', 'event', 'show', 59, 1, NULL, 67, 1, 0, NULL),
(2, 'default.edit.label', 'event.label', 'Edit event', 'event', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(3, 'eventDate.label', NULL, 'Event date overview', 'eventDate', 'show', 60, 1, NULL, 67, 1, 0, NULL),
(4, 'default.edit.label', 'eventDate.label', 'Edit event date', 'eventDate', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(5, 'default.create.label', 'event.label', 'Create event', 'event', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(6, 'default.create.label', 'eventDate.label', 'Create event date', 'eventDate', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(7, 'default.create.label', 'network.label', 'Create network', 'network', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(8, 'default.create.label', 'title.label', 'Create title', 'title', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(10, 'default.edit.label', 'network.label', 'Edit network', 'network', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(11, 'network.label', NULL, 'Network overview', 'network', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(12, 'default.edit.label', 'title.label', 'Edit title', 'title', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(13, 'title.label', NULL, 'Title overview', 'title', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(14, 'default.create.label', 'room.label', 'Create room', 'room', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(15, 'default.create.label', 'equipment.label', 'Create equipment', 'equipment', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(16, 'default.edit.label', 'room.label', 'Edit room', 'room', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(17, 'default.edit.label', 'equipment.label', 'Edit equipment', 'equipment', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(18, 'room.label', NULL, 'Room overview', 'room', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(19, 'equipment.label', NULL, 'Equipment overview', 'equipment', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(20, 'title.multiple.label', NULL, 'Titles', 'title', 'list', 65, 1, NULL, 66, 1, 0, NULL),
(21, 'room.multiple.label', NULL, 'Rooms', 'room', 'list', 25, 1, NULL, NULL, 1, 0, 'sort_0=roomNumber:asc;roomName:asc'),
(22, 'network.multiple.label', NULL, 'Networks', 'network', 'list', 10, 1, NULL, NULL, 1, 0, 'sort_0=showOnline:desc;name:asc'),
(23, 'equipment.multiple.label', NULL, 'Equipment', 'equipment', 'list', 60, 1, NULL, 66, 1, 0, NULL),
(26, 'session.multiple.label', NULL, 'Sessions', 'session', 'list', 15, 1, NULL, NULL, 1, 0, 'sort_0=code:asc;name:asc'),
(27, 'default.edit.label', 'session.label', 'Edit session', 'session', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(28, 'default.create.label', 'session.label', 'Create session', 'session', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(29, 'session.label', NULL, 'Session overview', 'session', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(30, 'default.create.label', 'feeState.label', 'Create fee state', 'fee', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(31, 'default.edit.label', 'feeState.label', 'Edit fee state', 'fee', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(32, 'feeState.multiple.label', NULL, 'Fee states', 'fee', 'list', 55, 1, NULL, 66, 1, 0, NULL),
(33, 'default.create.label', 'participantState.label', 'Create participant state', 'participantState', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(34, 'default.edit.label', 'participantState.label', 'Edit participant state', 'participantState', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(35, 'participantState.label', NULL, 'Participant state overview', 'participantState', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(36, 'participantState.multiple.label', NULL, 'Participant states', 'participantState', 'list', 50, 1, NULL, 66, 1, 0, NULL),
(37, 'participantDate.multiple.label', NULL, 'Participants', 'participant', 'list', 5, 1, NULL, NULL, 1, 0, NULL),
(38, 'participantDate.label', NULL, 'User overview', 'participant', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(40, 'default.create.label', 'emailTemplate.label', 'Create email template', 'emailTemplate', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(41, 'default.edit.label', 'emailTemplate.label', 'Edit email template', 'emailTemplate', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(42, 'emailTemplate.multiple.label', NULL, 'Email templates', 'emailTemplate', 'list', 70, 1, NULL, 66, 1, 0, NULL),
(43, 'emailTemplate.label', NULL, 'Show email template', 'emailTemplate', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(44, 'default.create.label', 'emailCode.label', 'Create email code', 'emailCode', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(45, 'default.edit.label', 'emailCode.label', 'Edit email code', 'emailCode', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(46, 'emailCode.multiple.label', NULL, 'List email code', 'emailCode', 'list', 40, 1, NULL, 67, 1, 0, NULL),
(47, 'emailCode.label', NULL, 'Show email code', 'emailCode', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(48, 'setting.multiple.label', NULL, 'Settings', 'setting', 'list', 100, 1, NULL, 67, 1, 0, NULL),
(49, 'default.edit.label', 'setting.label', 'Edit setting', 'setting', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(50, 'default.create.label', 'requestMap.label', 'Create request map', 'requestmap', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(51, 'default.edit.label', 'requestMap.label', 'Edit request map', 'requestmap', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(52, 'requestMap.multiple.label', NULL, 'Request maps', 'requestmap', 'list', 80, 1, NULL, 67, 1, 0, NULL),
(53, 'requestMap.label', NULL, 'Show request map', 'requestmap', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(54, 'extra.multiple.label', NULL, 'Extras', 'extra', 'list', 45, 1, NULL, 66, 1, 0, NULL),
(55, 'default.list.label', 'participantDate.invitationLetter.label', 'Invitation letter list', 'participant', 'invitations', 130, 1, NULL, 68, 1, 0, 'sort_0=firstName:asc;lastName:asc'),
(56, 'default.list.label', 'participantDate.lowerFee.label', 'Lower fee list', 'participant', 'lowerFee', 135, 1, NULL, 68, 1, 0, 'sort_0=firstName:asc;lastName:asc'),
(58, 'default.plan.label', 'session.multiple.label', 'Plan sessions', 'session', 'plan', 30, 1, NULL, NULL, 1, 0, NULL),
(59, 'default.send.label', 'email.label', 'Send e-mail', 'email', 'list', 35, 1, NULL, NULL, 1, 0, NULL),
(60, 'default.send.label', 'email.label', 'Send e-mail', 'email', 'send', 999, 0, NULL, NULL, 1, 0, NULL),
(61, 'group.multiple.label', NULL, 'Authorization groups', 'authGroup', 'list', 20, 1, NULL, 67, 1, 0, NULL),
(62, 'default.create.label', 'group.label', 'Create group', 'authGroup', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(63, 'default.edit.label', 'group.label', 'Edit group', 'authGroup', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(64, 'group.label', NULL, 'Group overview', 'authGroup', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(65, 'user.auth.label', NULL, 'Users', 'userAuth', 'list', 120, 1, 'title_code was: user.multiple.label', 67, 1, 0, 'sort_0=firstName:asc;lastName:asc'),
(66, 'default.edit.menu.label', NULL, 'Edit menu  »', NULL, NULL, 40, 1, NULL, NULL, 1, 0, NULL),
(67, 'default.admin.menu.label', NULL, 'Admin menu »', NULL, NULL, 200, 1, NULL, NULL, 1, 0, NULL),
(68, 'default.overview.menu.label', NULL, 'Overviews menu »', NULL, NULL, 110, 1, NULL, NULL, 1, 0, NULL),
(69, 'user.label', NULL, 'Show user', 'userAuth', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(70, 'default.edit.label', 'user.label', 'Edit user', 'userAuth', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(71, 'default.create.label', 'extra.label', 'Create extra', 'extra', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(72, 'default.edit.label', 'extra.label', 'Edit extra', 'extra', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(73, 'extra.label', NULL, 'Extra overview', 'extra', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(74, 'paperState.multiple.label', NULL, 'Paper states', 'paperState', 'list', 53, 1, NULL, 66, 1, 0, NULL),
(75, 'default.create.label', 'paperState.label', 'Create paper state', 'paperState', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(76, 'default.edit.label', 'paperState.label', 'Edit paper state', 'paperState', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(77, 'paperState.label', NULL, 'Show paper state', 'paperState', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(78, 'participantType.multiple.label', NULL, 'Participant types', 'participantType', 'list', 54, 1, NULL, 66, 1, 0, NULL),
(79, 'default.create.label', 'participantType.label', 'Create participant type', 'participantType', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(80, 'default.edit.label', 'participantType.label', 'Edit participant type', 'participantType', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(81, 'participantType.label', NULL, 'Show participant type', 'participantType', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(82, 'sessionDateTime.multiple.label', NULL, 'Dates/times', 'dateTime', 'list', 68, 1, NULL, 66, 1, 0, NULL),
(83, 'default.create.label', 'sessionDateTime.label', 'Create date/time', 'dateTime', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(84, 'default.edit.label', 'sessionDateTime.label', 'Edit date/time', 'dateTime', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(85, 'sessionDateTime.label', NULL, 'Show date/time', 'dateTime', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(86, 'volunteering.multiple.label', NULL, 'Volunteering', 'volunteering', 'list', 78, 1, NULL, 66, 1, 0, NULL),
(87, 'default.create.label', 'volunteering.label', 'Create volunteering job', 'volunteering', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(88, 'default.edit.label', 'volunteering.label', 'Edit volunteering job', 'volunteering', 'edit', 999, 0, NULL, NULL, 1, 0, NULL),
(89, 'volunteering.label', NULL, 'Show volunteering job', 'volunteering', 'show', 999, 0, NULL, NULL, 1, 0, NULL),
(90, 'default.list.label', 'extra.label', 'Extra list (final)', 'participant', 'extras', 125, 1, NULL, 68, 1, 0, 'sort_0=firstName:asc;lastName:asc'),
(91, 'default.create.label', 'user.label', 'Create user', 'userAuth', 'create', 999, 0, NULL, NULL, 1, 0, NULL),
(92, 'email.test.label', NULL, 'Send test mail', 'email', 'test', 999, 0, NULL, NULL, 1, 0, NULL),
(93, 'default.misc.menu.label', NULL, 'Misc »', NULL, NULL, 150, 1, NULL, NULL, 1, 0, NULL),
(94, 'default.change.label', 'paperState.label', 'Change paper state', 'participant', 'paperstate', 820, 1, NULL, 93, 1, 0, NULL),
(95, 'misc.lastNameUpperCase', NULL, 'Last name lower/upper case', 'misc', 'lastNameUpperCase', 860, 1, NULL, 93, 1, 0, NULL),
(96, 'misc.firstNameUpperCase', NULL, 'First name lower/upper case', 'misc', 'firstNameUpperCase', 840, 1, NULL, 93, 1, 0, NULL),
(97, 'misc.cityUpperCase', NULL, 'City lower/upper case', 'misc', 'cityUpperCase', 830, 1, NULL, 93, 1, 0, NULL),
(98, 'misc.organisationUpperCase', NULL, 'Organisation lower/upper case', 'misc', 'orgUpperCase', 885, 1, NULL, 93, 1, 0, NULL),
(99, 'misc.sessionSameName', NULL, 'Sessions with same name', 'misc', 'sessionSameName', 900, 1, NULL, 93, 1, 0, NULL),
(100, 'misc.sessionWithoutNetwork', NULL, 'Sessions without network', 'misc', 'sessionNoNetwork', 905, 1, NULL, 93, 1, 0, NULL),
(101, 'misc.sessionCoAuthor', NULL, 'Sessions with co-authors', 'misc', 'sessionCoAuthor', 895, 1, NULL, 93, 1, 0, NULL),
(102, 'misc.paperSameTitle', NULL, 'Papers with same title', 'misc', 'paperSameTitle', 890, 1, NULL, 93, 1, 0, NULL),
(103, 'misc.multiplePapers', NULL, 'Multiple papers', 'misc', 'multiplePapers', 875, 1, NULL, 93, 1, 0, NULL),
(104, 'misc.authorDelSession', NULL, 'Authors in deleted sessions', 'misc', 'authorDelSession', 810, 1, NULL, 93, 1, 0, NULL),
(105, 'misc.award', NULL, 'Award', 'misc', 'award', 805, 1, NULL, 93, 1, 0, NULL),
(106, 'participantDate.gender.unknown.label', NULL, 'Gender unknown', 'participant', 'gender', 850, 1, NULL, 93, 1, 0, 'sort_0=firstName:asc;lastName:asc'),
(107, 'session.mismatch.label', NULL, 'Mismatch session/paper state', 'session', 'paperMismatch', 865, 1, NULL, 93, 1, 0, NULL),
(108, 'misc.notRegisteredChairs', NULL, 'Not registered chairs', 'misc', 'notRegisteredChairs', 880, 1, NULL, 93, 1, 0, NULL),
(109, 'misc.multipleAccPapers', NULL, 'Multiple accepted papers', 'misc', 'multipleAccPapers', 870, 1, NULL, 93, 1, 0, NULL),
(110, 'default.export.menu.label', NULL, 'Export', 'export', 'index', 999, 1, NULL, NULL, 1, 0, NULL),
(111, 'misc.participantsSameName', NULL, 'Participants with the same name', 'misc', 'participantsSameName', 893, 1, NULL, 93, 1, 0, NULL),
(112, 'misc.chairs', NULL, 'All chairs', 'misc', 'chairs', 800, 1, NULL, 93, 1, 0, NULL),
(113, 'misc.sessionsWithoutOrganizer', NULL, 'Sessions without organizer', 'misc', 'sessionsNoOrganizer', 910, 1, NULL, 93, 1, 0, NULL);

CREATE TABLE IF NOT EXISTS `papers` (
  `paper_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `paper_state_id` bigint(20) NOT NULL DEFAULT '1',
  `session_id` bigint(20) DEFAULT NULL,
  `date_id` bigint(20) DEFAULT NULL,
  `title` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
  `co_authors` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `abstract` text COLLATE utf8_unicode_ci NOT NULL,
  `comment` text COLLATE utf8_unicode_ci,
  `network_proposal_id` bigint(20) DEFAULT NULL,
  `session_proposal` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `proposal_description` text COLLATE utf8_unicode_ci,
  `filename` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `content_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `filesize` bigint(20) DEFAULT NULL,
  `file` mediumblob,
  `equipment_comment` text COLLATE utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `added_by` bigint(20) DEFAULT '0',
  `mail_paper_state` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`paper_id`),
  KEY `user_id` (`user_id`),
  KEY `paper_state_id` (`paper_state_id`),
  KEY `session_id` (`session_id`),
  KEY `network_proposal_id` (`network_proposal_id`),
  KEY `idx_enabled_deleted` (`enabled`,`deleted`),
  KEY `idx_date_id` (`date_id`),
  KEY `date_id` (`date_id`,`deleted`,`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `paper_equipment` (
  `paper_id` bigint(20) NOT NULL,
  `equipment_id` bigint(20) NOT NULL,
  PRIMARY KEY (`paper_id`,`equipment_id`),
  KEY `equipment_id` (`equipment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `paper_states` (
  `paper_state_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `description` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `short_description` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `session_state_trigger` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`paper_state_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=11 ;

INSERT INTO `paper_states` (`paper_state_id`, `event_id`, `description`, `short_description`, `session_state_trigger`, `enabled`, `deleted`) VALUES
(1, NULL, 'New Paper', 'new', 1, 1, 0),
(2, NULL, 'Paper Accepted', 'acc', 0, 1, 0),
(3, NULL, 'Paper Not Accepted', 'not', 0, 1, 0),
(4, NULL, 'Paper In Consideration', 'in c', 0, 1, 0),
(6, NULL, 'No Paper', 'no', 0, 1, 0);

CREATE TABLE IF NOT EXISTS `participant_date` (
  `participant_date_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `date_id` bigint(20) NOT NULL DEFAULT '0',
  `participant_state_id` bigint(20) NOT NULL DEFAULT '0',
  `fee_state_id` bigint(20) NOT NULL DEFAULT '0',
  `payment_id` bigint(20) NOT NULL DEFAULT '0',
  `date_added` date NOT NULL,
  `invitation_letter` tinyint(1) NOT NULL DEFAULT '0',
  `invitation_letter_sent` tinyint(1) NOT NULL DEFAULT '0',
  `lower_fee_requested` tinyint(1) NOT NULL DEFAULT '0',
  `lower_fee_answered` tinyint(1) NOT NULL DEFAULT '0',
  `lower_fee_text` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `student` tinyint(1) NOT NULL DEFAULT '0',
  `student_confirmed` tinyint(1) NOT NULL DEFAULT '0',
  `award` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `added_by` bigint(20) DEFAULT '0',
  PRIMARY KEY (`participant_date_id`),
  UNIQUE KEY `user_id` (`user_id`,`date_id`),
  KEY `participant_state_id` (`participant_state_id`),
  KEY `fee_state_id` (`fee_state_id`),
  KEY `payment_id` (`payment_id`),
  KEY `date_id_2` (`date_id`,`deleted`,`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `participant_date_extra` (
  `participant_date_id` bigint(20) NOT NULL,
  `extra_id` bigint(20) NOT NULL,
  PRIMARY KEY (`participant_date_id`,`extra_id`),
  KEY `extra_id` (`extra_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `participant_not_present` (
  `user_id` bigint(20) NOT NULL,
  `session_datetime_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`session_datetime_id`),
  KEY `session_datetime_id` (`session_datetime_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `participant_states` (
  `participant_state_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `participant_state` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`participant_state_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1000 ;

INSERT INTO `participant_states` (`participant_state_id`, `event_id`, `participant_state`, `enabled`, `deleted`) VALUES
(1, NULL, 'Participant data checked', 1, 0),
(2, NULL, 'Participant', 1, 0),
(3, NULL, 'Will be removed', 1, 0),
(4, NULL, 'Removed: Cancelled', 1, 0),
(5, NULL, 'Removed: Double entry', 1, 0),
(6, NULL, 'No show', 1, 0),
(7, NULL, 'Unclear', 1, 0),
(10, NULL, 'New participant', 1, 0),
(999, NULL, 'Did not finish registration', 1, 0);

CREATE TABLE IF NOT EXISTS `participant_types` (
  `participant_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `type` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `importance` int(11) NOT NULL DEFAULT '0',
  `with_paper` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`participant_type_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=11 ;

INSERT INTO `participant_types` (`participant_type_id`, `event_id`, `type`, `importance`, `with_paper`, `enabled`, `deleted`) VALUES
(6, NULL, 'Chair', 5, 0, 1, 0),
(7, NULL, 'Organizer', 4, 0, 1, 0),
(8, NULL, 'Author', 3, 1, 1, 0),
(9, NULL, 'Co-Author', 2, 0, 1, 0),
(10, NULL, 'Discussant', 1, 0, 1, 0);

CREATE TABLE IF NOT EXISTS `participant_type_rules` (
  `participant_type_rule_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `participant_type_1_id` bigint(20) NOT NULL,
  `participant_type_2_id` bigint(20) NOT NULL,
  `event_id` bigint(20) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`participant_type_rule_id`),
  UNIQUE KEY `participant_type_1_id` (`participant_type_1_id`,`participant_type_2_id`,`event_id`),
  KEY `participant_type_2_id` (`participant_type_2_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `participant_volunteering` (
  `participant_date_id` bigint(20) NOT NULL,
  `volunteering_id` bigint(20) NOT NULL,
  `network_id` bigint(20) NOT NULL,
  PRIMARY KEY (`participant_date_id`,`volunteering_id`,`network_id`),
  KEY `volunteering_id` (`volunteering_id`),
  KEY `network_id` (`network_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `request_map` (
  `request_map_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `config_attribute` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`request_map_id`),
  UNIQUE KEY `url` (`url`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=8 ;

INSERT INTO `request_map` (`request_map_id`, `url`, `config_attribute`) VALUES
(1, '/login/*', 'permitAll'),
(2, '/logout/*', 'permitAll'),
(3, 'favicon.ico', 'permitAll'),
(4, '/favicon.ico', 'permitAll'),
(5, '//favicon.ico', 'permitAll'),
(6, '/css/*', 'permitAll'),
(7, '/**', 'hasRole(''user'')');

CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `description` text COLLATE utf8_unicode_ci,
  `full_rights` tinyint(1) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role` (`role`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=4 ;

INSERT INTO `roles` (`role_id`, `role`, `description`, `full_rights`) VALUES
(1, 'superAdmin', NULL, 1),
(2, 'admin', NULL, 0),
(3, 'user', NULL, 0);

CREATE TABLE IF NOT EXISTS `rooms` (
  `room_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_id` bigint(20) DEFAULT NULL,
  `room_name` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `room_number` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `OLDGCUnumber_of_seets` int(11) NOT NULL DEFAULT '0',
  `comment` text COLLATE utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `original_roomid` tinyint(4) NOT NULL DEFAULT '0',
  `original_dateid` tinyint(4) NOT NULL DEFAULT '0',
  `number_of_seats` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`room_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `room_sessiondatetime_equipment` (
  `room_id` bigint(20) NOT NULL,
  `date_id` bigint(20) NOT NULL,
  `session_datetime_id` bigint(20) NOT NULL,
  `equipment_id` bigint(20) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`room_id`,`session_datetime_id`,`equipment_id`),
  KEY `session_datetime_id` (`session_datetime_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `room_id` (`room_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `sent_emails` (
  `sent_email_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `date_id` bigint(20) DEFAULT NULL,
  `from_name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `from_email` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `subject` varchar(78) COLLATE utf8_unicode_ci NOT NULL,
  `body` longtext COLLATE utf8_unicode_ci NOT NULL,
  `date_time_sent` datetime DEFAULT NULL,
  `num_tries` int(11) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `query_type` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`sent_email_id`),
  KEY `user_id` (`user_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `sessions` (
  `session_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_id` bigint(20) DEFAULT NULL,
  `session_code` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `session_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `session_abstract` text COLLATE utf8_unicode_ci,
  `session_comment` text COLLATE utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `added_by` bigint(20) DEFAULT '0',
  `session_state_id` bigint(20) NOT NULL DEFAULT '1',
  `mail_session_state` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`session_id`),
  KEY `session_state_id` (`session_state_id`),
  KEY `date_id` (`date_id`,`session_code`,`session_name`,`deleted`,`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `session_datetime` (
  `session_datetime_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_id` bigint(20) NOT NULL,
  `day_id` bigint(20) NOT NULL,
  `index_number` int(11) NOT NULL,
  `period` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`session_datetime_id`),
  KEY `day_id` (`day_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `session_in_network` (
  `network_id` bigint(20) NOT NULL,
  `session_id` bigint(20) NOT NULL,
  `added_by` bigint(20) DEFAULT '0',
  PRIMARY KEY (`network_id`,`session_id`),
  KEY `session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `session_participant` (
  `session_participant_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `session_id` bigint(20) NOT NULL,
  `participant_type_id` bigint(20) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `added_by` bigint(20) DEFAULT '0',
  PRIMARY KEY (`session_participant_id`),
  KEY `user_id` (`user_id`),
  KEY `session_id` (`session_id`),
  KEY `participant_type_id` (`participant_type_id`),
  KEY `idx_user_session_type` (`user_id`,`session_id`,`participant_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `session_room_datetime` (
  `room_id` bigint(20) NOT NULL,
  `date_id` bigint(20) NOT NULL,
  `session_datetime_id` bigint(20) NOT NULL,
  `session_id` bigint(20) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`room_id`,`session_datetime_id`,`session_id`),
  KEY `session_datetime_id` (`session_datetime_id`),
  KEY `session_id` (`session_id`),
  KEY `date_id` (`date_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `session_states` (
  `session_state_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `description` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `short_description` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `corresponding_paper_state_id` bigint(20) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`session_state_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=5 ;

INSERT INTO `session_states` (`session_state_id`, `event_id`, `description`, `short_description`, `corresponding_paper_state_id`, `enabled`, `deleted`) VALUES
(1, NULL, 'New Session', 'new', 1, 1, 0),
(2, NULL, 'Session Accepted', 'acc', 2, 1, 0),
(3, NULL, 'Session Not Accepted', 'not', 3, 1, 0),
(4, NULL, 'Session In Consideration', 'in c', 4, 1, 0);

CREATE TABLE IF NOT EXISTS `settings` (
  `setting_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `property` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `value` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `event_id` bigint(20) DEFAULT NULL,
  `show_in_backend` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`setting_id`),
  KEY `settings_event_id_idx` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=30 ;

INSERT INTO `settings` (`setting_id`, `property`, `value`, `event_id`, `show_in_backend`, `enabled`, `deleted`) VALUES
(1, 'last_updated', '13-09-11', NULL, 1, 1, 0),
(3, 'max_papers_per_person_per_session', NULL, NULL, 1, 1, 0),
(4, 'salt', 'l806hw0aJp6PcXKh3aelytHM0C', NULL, 0, 1, 0),
(5, 'role_hierarchy', 'superAdmin > admin admin > user', NULL, 1, 1, 0),
(6, 'show_programme_online', '0', NULL, 1, 1, 0),
(7, 'email_max_num_tries', '3', NULL, 1, 1, 0),
(8, 'email_min_minutes_between_sending', '15', NULL, 1, 1, 0),
(9, 'email_max_num_emails_per_session', '200', NULL, 1, 1, 0),
(10, 'disable_email_sessions', '0', NULL, 1, 1, 0),
(11, 'default_organisation_email', NULL, NULL, 1, 1, 0),
(12, 'email_address_info_errors', NULL, NULL, 1, 1, 0),
(14, 'web_address', NULL, NULL, 1, 1, 0),
(20, 'change_user', NULL, NULL, 1, 1, 0),
(22, 'dont_send_emails_to', NULL, NULL, 1, 1, 0),
(23, 'application_title', 'Conference Management System', NULL, 1, 1, 0),
(24, 'banner_img', 'banner-esshc.jpg', NULL, 1, 1, 0),
(25, 'banner_bg_img', 'bannerbg-esshc.jpg', NULL, 1, 1, 0),
(26, 'label_color', '#666666', NULL, 1, 1, 0),
(27, 'main_color_light', '#7F9DB9', NULL, 1, 1, 0),
(28, 'main_color_dark', '#225496', NULL, 1, 1, 0),
(29, 'main_color_bg', '#D8E2EB', NULL, 1, 1, 0);

CREATE TABLE IF NOT EXISTS `statistics` (
  `statistic_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_id` bigint(20) NOT NULL,
  `property` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `value` text COLLATE utf8_unicode_ci NOT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`statistic_id`),
  UNIQUE KEY `STATS_ALTERNATE_KEY` (`date_id`,`property`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `statistics_templates` (
  `statistics_template_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `template` text COLLATE utf8_unicode_ci,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`statistics_template_id`),
  KEY `statistics_templates_ibfk_1` (`event_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=2 ;

INSERT INTO `statistics_templates` (`statistics_template_id`, `event_id`, `template`, `enabled`, `deleted`) VALUES
(1, NULL, '<table border=0 cellspacing=1 cellpadding=1>\r\n<tr>\r\n<td valign=top>\r\n\r\n<strong>Statistics</strong><br>\r\n<em>(only accepted participants/papers/sessions/...)</em><br>\r\n<em>Last updated: [lastupdated]</em><br>\r\n<br>\r\n\r\n<strong>Participants:</strong> [participants]<br>\r\n<br>\r\n\r\n<strong>Gender</strong><br>\r\n<strong>Male:</strong> [gendermale] ([percentagegendermale]%)<br>\r\n<strong>Female:</strong> [genderfemale] ([percentagegenderfemale]%)<br>\r\n<strong>Unknown:</strong> [genderunknown] ([percentagegenderunknown]%)<br>\r\n<br>\r\n\r\n<strong>Student:</strong> [students]<br>\r\n<strong>Award:</strong> [awards]<br>\r\n<br>\r\n\r\n<strong>Countries:</strong> [countries]<br>\r\n<br>\r\n\r\n<strong>Type of participants (participant can have multiple types)</strong><br>\r\n<strong>Chairs:</strong> [participanttypechairs]<br>\r\n<strong>Organizers:</strong> [participanttypeorganizers]<br>\r\n<strong>Authors:</strong> [participanttypeauthors]<br>\r\n<strong>Co-authors:</strong> [participanttypecoauthors]<br>\r\n<strong>Discussants:</strong> [participanttypediscussants]<br>\r\n<br>\r\n\r\n<strong>Sessions:</strong> (total: [sessionstotal])<br>\r\nNew sessions: [sessionsnew]<br>\r\nAccepted sessions: [sessionsaccepted]<br>\r\nNot accepted sessions: [sessionsnotaccepted]<br>\r\nIn consideration: [sessionsinconsideration]<br>\r\n<br>\r\n\r\n<strong>Papers:</strong> (total: [paperstotal])<br>\r\nNew papers: [papersnew]<br>\r\nAccepted papers: [papersaccepted]<br>\r\nNot accepted papers: [papersnotaccepted]<br>\r\nIn consideration: [papersinconsideration]<br>\r\n<br>\r\n\r\n<strong>Participants per network:</strong><br>\r\n[participantspernetwork]\r\n<br>\r\n\r\n\r\n</td>\r\n<td valign=top>\r\n\r\n\r\n<strong>Top 20 countries:</strong><br>\r\n[top20countries]\r\n<br>\r\n\r\n<strong>Top 20 cities:</strong><br>\r\n[top20cities]\r\n<br>\r\n\r\n<strong>Top 20 organisations:</strong><br>\r\n[top20organisations]\r\n<br>\r\n\r\n\r\n</td>\r\n</tr>\r\n</table>\r\n', 1, 0);

CREATE TABLE IF NOT EXISTS `titles` (
  `title_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) DEFAULT NULL,
  `title` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`title_id`),
  KEY `titles_event_id_idx` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `lastname` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `firstname` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `gender` enum('M','F') COLLATE utf8_unicode_ci DEFAULT NULL,
  `title` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `address` text COLLATE utf8_unicode_ci,
  `city` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country_id` bigint(20) DEFAULT NULL,
  `language` varchar(10) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'en',
  `password` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `salt` varchar(26) COLLATE utf8_unicode_ci DEFAULT NULL,
  `request_code` varchar(26) COLLATE utf8_unicode_ci DEFAULT NULL,
  `request_code_valid_until` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fax` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mobile` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `organisation` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `department` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `extra_info` text COLLATE utf8_unicode_ci,
  `date_added` date NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `added_by` bigint(20) DEFAULT '0',
  `newsletter` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `users_country_id_idx` (`country_id`),
  KEY `lastname` (`lastname`,`firstname`,`deleted`,`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `users_groups` (
  `user_id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`group_id`),
  KEY `users_groups_group_id_fk` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `users_pages` (
  `user_id` bigint(20) NOT NULL,
  `page_id` bigint(20) NOT NULL,
  `denied` tinyint(1) NOT NULL DEFAULT '0',
  `event_id` bigint(20) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`page_id`),
  KEY `users_pages_page_id_idx` (`page_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `users_roles` (
  `user_role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `event_id` bigint(20) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_role_id`),
  KEY `users_roles_user_id_idx` (`user_id`),
  KEY `users_roles_role_id_idx` (`role_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `volunteering` (
  `volunteering_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `event_id` bigint(20) NOT NULL,
  `description` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`volunteering_id`),
  KEY `event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;
CREATE TABLE IF NOT EXISTS `vw_accepted_participants` (
`user_id` bigint(20)
,`lastname` varchar(100)
,`firstname` varchar(100)
,`date_id` bigint(20)
);CREATE TABLE IF NOT EXISTS `vw_dbl_acc_papers` (
`user_id` bigint(20)
,`date_id` bigint(20)
);CREATE TABLE IF NOT EXISTS `vw_papers_end` (
`paper_id` bigint(20)
,`user_id` bigint(20)
,`paper_state_id` bigint(20)
,`session_id` bigint(20)
,`date_id` bigint(20)
,`title` varchar(500)
,`co_authors` varchar(500)
,`abstract` text
,`comment` text
,`network_proposal_id` bigint(20)
,`session_proposal` varchar(500)
,`proposal_description` text
,`filename` varchar(500)
,`content_type` varchar(100)
,`filesize` bigint(20)
,`file` mediumblob
,`equipment_comment` text
,`enabled` tinyint(1)
,`deleted` tinyint(1)
,`added_by` bigint(20)
,`mail_paper_state` tinyint(1)
);CREATE TABLE IF NOT EXISTS `vw_participant_date_end` (
`participant_date_id` bigint(20)
,`user_id` bigint(20)
,`date_id` bigint(20)
,`participant_state_id` bigint(20)
,`fee_state_id` bigint(20)
,`payment_id` bigint(20)
,`date_added` date
,`invitation_letter` tinyint(1)
,`invitation_letter_sent` tinyint(1)
,`lower_fee_requested` tinyint(1)
,`lower_fee_answered` tinyint(1)
,`lower_fee_text` varchar(255)
,`student` tinyint(1)
,`student_confirmed` tinyint(1)
,`award` tinyint(1)
,`enabled` tinyint(1)
,`deleted` tinyint(1)
,`added_by` bigint(20)
);CREATE TABLE IF NOT EXISTS `vw_users_end` (
`user_id` bigint(20)
,`email` varchar(100)
,`lastname` varchar(100)
,`firstname` varchar(100)
,`gender` enum('M','F')
,`title` varchar(20)
,`address` text
,`city` varchar(100)
,`country_id` bigint(20)
,`language` varchar(10)
,`password` varchar(128)
,`salt` varchar(26)
,`request_code` varchar(26)
,`request_code_valid_until` varchar(20)
,`phone` varchar(50)
,`fax` varchar(50)
,`mobile` varchar(50)
,`organisation` varchar(255)
,`department` varchar(255)
,`extra_info` text
,`date_added` date
,`enabled` tinyint(1)
,`deleted` tinyint(1)
,`added_by` bigint(20)
);DROP TABLE IF EXISTS `vw_accepted_participants`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_accepted_participants` AS select `users`.`user_id` AS `user_id`,`users`.`lastname` AS `lastname`,`users`.`firstname` AS `firstname`,`participant_date`.`date_id` AS `date_id` from (`users` join `participant_date` on(((`users`.`user_id` = `participant_date`.`user_id`) and (`users`.`enabled` = 1) and (`users`.`deleted` = 0) and (`participant_date`.`enabled` = 1) and (`participant_date`.`deleted` = 0) and (`participant_date`.`participant_state_id` in (1,2))))) order by `users`.`lastname`,`users`.`firstname`;
DROP TABLE IF EXISTS `vw_dbl_acc_papers`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_dbl_acc_papers` AS select `vw_papers_end`.`user_id` AS `user_id`,`vw_papers_end`.`date_id` AS `date_id` from ((`vw_papers_end` join `session_participant` on((`vw_papers_end`.`user_id` = `session_participant`.`user_id`))) join `participant_types` on((`session_participant`.`participant_type_id` = `participant_types`.`participant_type_id`))) where ((`vw_papers_end`.`session_id` = `session_participant`.`session_id`) and (`vw_papers_end`.`paper_state_id` = 2) and ((`participant_types`.`participant_type_id` in (3,8)) or (`participant_types`.`type` = _utf8'Author'))) group by `vw_papers_end`.`user_id`,`vw_papers_end`.`date_id` having (count(0) > 1);
DROP TABLE IF EXISTS `vw_papers_end`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_papers_end` AS select `papers`.`paper_id` AS `paper_id`,`papers`.`user_id` AS `user_id`,`papers`.`paper_state_id` AS `paper_state_id`,`papers`.`session_id` AS `session_id`,`papers`.`date_id` AS `date_id`,`papers`.`title` AS `title`,`papers`.`co_authors` AS `co_authors`,`papers`.`abstract` AS `abstract`,`papers`.`comment` AS `comment`,`papers`.`network_proposal_id` AS `network_proposal_id`,`papers`.`session_proposal` AS `session_proposal`,`papers`.`proposal_description` AS `proposal_description`,`papers`.`filename` AS `filename`,`papers`.`content_type` AS `content_type`,`papers`.`filesize` AS `filesize`,`papers`.`file` AS `file`,`papers`.`equipment_comment` AS `equipment_comment`,`papers`.`enabled` AS `enabled`,`papers`.`deleted` AS `deleted`,`papers`.`added_by` AS `added_by`,`papers`.`mail_paper_state` AS `mail_paper_state` from `papers` where ((`papers`.`enabled` = 1) and (`papers`.`deleted` = 0));
DROP TABLE IF EXISTS `vw_participant_date_end`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_participant_date_end` AS select `participant_date`.`participant_date_id` AS `participant_date_id`,`participant_date`.`user_id` AS `user_id`,`participant_date`.`date_id` AS `date_id`,`participant_date`.`participant_state_id` AS `participant_state_id`,`participant_date`.`fee_state_id` AS `fee_state_id`,`participant_date`.`payment_id` AS `payment_id`,`participant_date`.`date_added` AS `date_added`,`participant_date`.`invitation_letter` AS `invitation_letter`,`participant_date`.`invitation_letter_sent` AS `invitation_letter_sent`,`participant_date`.`lower_fee_requested` AS `lower_fee_requested`,`participant_date`.`lower_fee_answered` AS `lower_fee_answered`,`participant_date`.`lower_fee_text` AS `lower_fee_text`,`participant_date`.`student` AS `student`,`participant_date`.`student_confirmed` AS `student_confirmed`,`participant_date`.`award` AS `award`,`participant_date`.`enabled` AS `enabled`,`participant_date`.`deleted` AS `deleted`,`participant_date`.`added_by` AS `added_by` from `participant_date` where ((`participant_date`.`enabled` = 1) and (`participant_date`.`deleted` = 0));
DROP TABLE IF EXISTS `vw_users_end`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_users_end` AS select `users`.`user_id` AS `user_id`,`users`.`email` AS `email`,`users`.`lastname` AS `lastname`,`users`.`firstname` AS `firstname`,`users`.`gender` AS `gender`,`users`.`title` AS `title`,`users`.`address` AS `address`,`users`.`city` AS `city`,`users`.`country_id` AS `country_id`,`users`.`language` AS `language`,`users`.`password` AS `password`,`users`.`salt` AS `salt`,`users`.`request_code` AS `request_code`,`users`.`request_code_valid_until` AS `request_code_valid_until`,`users`.`phone` AS `phone`,`users`.`fax` AS `fax`,`users`.`mobile` AS `mobile`,`users`.`organisation` AS `organisation`,`users`.`department` AS `department`,`users`.`extra_info` AS `extra_info`,`users`.`date_added` AS `date_added`,`users`.`enabled` AS `enabled`,`users`.`deleted` AS `deleted`,`users`.`added_by` AS `added_by` from `users` where ((`users`.`enabled` = 1) and (`users`.`deleted` = 0));


ALTER TABLE `admin_pages`
  ADD CONSTRAINT `admin_pages_event_id_fk` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`),
  ADD CONSTRAINT `admin_pages_page_id_fk` FOREIGN KEY (`page_id`) REFERENCES `pages` (`page_id`);

ALTER TABLE `dates`
  ADD CONSTRAINT `dates_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `days`
  ADD CONSTRAINT `days_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `dynamic_pages`
  ADD CONSTRAINT `dynamic_pages_date_id_fk` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`),
  ADD CONSTRAINT `dynamic_pages_page_id_fk` FOREIGN KEY (`page_id`) REFERENCES `pages` (`page_id`);

ALTER TABLE `email_codes`
  ADD CONSTRAINT `email_codes_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

ALTER TABLE `email_templates`
  ADD CONSTRAINT `email_templates_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

ALTER TABLE `equipment`
  ADD CONSTRAINT `equipment_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

ALTER TABLE `extras`
  ADD CONSTRAINT `extras_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `fee_amounts`
  ADD CONSTRAINT `fee_amounts_ibfk_3` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fee_amounts_ibfk_4` FOREIGN KEY (`fee_state_id`) REFERENCES `fee_states` (`fee_state_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `fee_states`
  ADD CONSTRAINT `fee_states_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `groups`
  ADD CONSTRAINT `groups_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `groups_pages`
  ADD CONSTRAINT `groups_pages_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `groups_pages_ibfk_2` FOREIGN KEY (`page_id`) REFERENCES `pages` (`page_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `networks`
  ADD CONSTRAINT `networks_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`);

ALTER TABLE `networks_chairs`
  ADD CONSTRAINT `networks_chairs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `networks_chairs_ibfk_3` FOREIGN KEY (`network_id`) REFERENCES `networks` (`network_id`);

ALTER TABLE `pages`
  ADD CONSTRAINT `pages_parent_page_id_fk` FOREIGN KEY (`parent_page_id`) REFERENCES `pages` (`page_id`);

ALTER TABLE `papers`
  ADD CONSTRAINT `papers_ibfk_3` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`),
  ADD CONSTRAINT `papers_ibfk_4` FOREIGN KEY (`paper_state_id`) REFERENCES `paper_states` (`paper_state_id`),
  ADD CONSTRAINT `papers_ibfk_6` FOREIGN KEY (`network_proposal_id`) REFERENCES `networks` (`network_id`),
  ADD CONSTRAINT `papers_ibfk_7` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `papers_ibfk_8` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`session_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `paper_equipment`
  ADD CONSTRAINT `paper_equipment_ibfk_2` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`),
  ADD CONSTRAINT `paper_equipment_ibfk_3` FOREIGN KEY (`paper_id`) REFERENCES `papers` (`paper_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `paper_states`
  ADD CONSTRAINT `paper_states_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON UPDATE CASCADE;

ALTER TABLE `participant_date`
  ADD CONSTRAINT `participant_date_ibfk_2` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`),
  ADD CONSTRAINT `participant_date_ibfk_3` FOREIGN KEY (`participant_state_id`) REFERENCES `participant_states` (`participant_state_id`),
  ADD CONSTRAINT `participant_date_ibfk_4` FOREIGN KEY (`fee_state_id`) REFERENCES `fee_states` (`fee_state_id`),
  ADD CONSTRAINT `participant_date_ibfk_5` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `participant_date_extra`
  ADD CONSTRAINT `participant_date_extra_ibfk_5` FOREIGN KEY (`participant_date_id`) REFERENCES `participant_date` (`participant_date_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `participant_date_extra_ibfk_6` FOREIGN KEY (`extra_id`) REFERENCES `extras` (`extra_id`) ON DELETE CASCADE;

ALTER TABLE `participant_not_present`
  ADD CONSTRAINT `participant_not_present_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `participant_not_present_ibfk_4` FOREIGN KEY (`session_datetime_id`) REFERENCES `session_datetime` (`session_datetime_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `participant_states`
  ADD CONSTRAINT `participant_states_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

ALTER TABLE `participant_types`
  ADD CONSTRAINT `participant_types_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

ALTER TABLE `participant_type_rules`
  ADD CONSTRAINT `participant_type_rules_ibfk_6` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `participant_type_rules_ibfk_4` FOREIGN KEY (`participant_type_1_id`) REFERENCES `participant_types` (`participant_type_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `participant_type_rules_ibfk_5` FOREIGN KEY (`participant_type_2_id`) REFERENCES `participant_types` (`participant_type_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `participant_volunteering`
  ADD CONSTRAINT `participant_volunteering_ibfk_4` FOREIGN KEY (`participant_date_id`) REFERENCES `participant_date` (`participant_date_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `participant_volunteering_ibfk_5` FOREIGN KEY (`volunteering_id`) REFERENCES `volunteering` (`volunteering_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `participant_volunteering_ibfk_6` FOREIGN KEY (`network_id`) REFERENCES `networks` (`network_id`) ON DELETE CASCADE;

ALTER TABLE `rooms`
  ADD CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `room_sessiondatetime_equipment`
  ADD CONSTRAINT `room_sessiondatetime_equipment_ibfk_4` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `room_sessiondatetime_equipment_ibfk_5` FOREIGN KEY (`session_datetime_id`) REFERENCES `session_datetime` (`session_datetime_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `room_sessiondatetime_equipment_ibfk_6` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `room_sessiondatetime_equipment_ibfk_7` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `sessions`
  ADD CONSTRAINT `sessions_ibfk_2` FOREIGN KEY (`session_state_id`) REFERENCES `session_states` (`session_state_id`),
  ADD CONSTRAINT `sessions_ibfk_3` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `session_datetime`
  ADD CONSTRAINT `session_datetime_ibfk_1` FOREIGN KEY (`day_id`) REFERENCES `days` (`day_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `session_datetime_ibfk_2` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `session_in_network`
  ADD CONSTRAINT `session_in_network_ibfk_1` FOREIGN KEY (`network_id`) REFERENCES `networks` (`network_id`),
  ADD CONSTRAINT `session_in_network_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`session_id`) ON DELETE CASCADE;

ALTER TABLE `session_participant`
  ADD CONSTRAINT `session_participant_ibfk_3` FOREIGN KEY (`participant_type_id`) REFERENCES `participant_types` (`participant_type_id`),
  ADD CONSTRAINT `session_participant_ibfk_5` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `session_participant_ibfk_6` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`session_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `session_room_datetime`
  ADD CONSTRAINT `session_room_datetime_ibfk_4` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `session_room_datetime_ibfk_5` FOREIGN KEY (`session_datetime_id`) REFERENCES `session_datetime` (`session_datetime_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `session_room_datetime_ibfk_6` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`session_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `session_room_datetime_ibfk_7` FOREIGN KEY (`date_id`) REFERENCES `dates` (`date_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `session_states`
  ADD CONSTRAINT `session_states_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON UPDATE CASCADE;

ALTER TABLE `settings`
  ADD CONSTRAINT `settings_event_id_fk` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

ALTER TABLE `titles`
  ADD CONSTRAINT `titles_event_id_fk` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`);

ALTER TABLE `users`
  ADD CONSTRAINT `users_country_id_fk` FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`);

ALTER TABLE `users_groups`
  ADD CONSTRAINT `users_groups_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `users_groups_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `users_pages`
  ADD CONSTRAINT `users_pages_ibfk_3` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `users_pages_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `users_pages_ibfk_2` FOREIGN KEY (`page_id`) REFERENCES `pages` (`page_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `users_roles`
  ADD CONSTRAINT `users_roles_ibfk_3` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `users_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `users_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON UPDATE CASCADE;

ALTER TABLE `volunteering`
  ADD CONSTRAINT `volunteering_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
