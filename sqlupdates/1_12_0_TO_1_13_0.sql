#DATABASE UPDATE: 1.12.0 -> 1.13.0

#IF YOU ARE NOT USING HTTPS RUN:
#UPDATE emulator_settings SET `value` = '0' WHERE `key` LIKE 'camera.use.https';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES
    ('hotel.view.ltdcountdown.enabled', '1'),
    ('hotel.view.ltdcountdown.timestamp', '1519496132'),
    ('hotel.view.ltdcountdown.itemid', '10388'),
    ('hotel.view.ltdcountdown.pageid', '13'),
    ('hotel.view.ltdcountdown.itename', 'trophy_netsafety_0');

CREATE TABLE `users_ignored` (
    `user_id` int(11) NOT NULL,
    `target_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `users_ignored`
  ADD KEY `user_id` (`user_id`,`target_id`);
COMMIT;

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('io.client.multithreaded.handler', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.room.stickypole.prefix', '%timestamp%, %username%:\\r');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('support.ticket.picked.failed', 'Picking issue failedd: <br>Ticket already picked or does not exist!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('camera.error.creation', 'Failed to create your picture. *sadpanda*');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('scripter.warning.sticky.size', '%username% tried to create a sticky with %amount% characters where %limit% characters are allowed!');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.trading.requires.perk', '1');
ALTER TABLE `users_settings` ADD `perk_trade` ENUM('0','1') NOT NULL DEFAULT '0' COMMENT 'Defines if a player has obtained the perk TRADE. When hotel.trading.requires.perk is set to 1, this perk is required in order to trade. Perk is obtained from the talen track.' AFTER `allow_name_change`;
UPDATE users_settings SET perk_trade = '1' WHERE talent_track_citizenship_level >= (SELECT `level` FROM achievements_talents WHERE reward_perks LIKE '%TRADE%' ORDER BY level ASC LIMIT 1);
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.targetoffer.id', '1');

CREATE TABLE `catalog_target_offers` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `offer_code` varchar(32) NOT NULL,
 `title` varchar(128) NOT NULL DEFAULT '',
 `description` varchar(2048) NOT NULL DEFAULT '',
 `image` varchar(128) NOT NULL,
 `icon` varchar(128) NOT NULL,
 `end_timestamp` int(11) NOT NULL,
 `credits` int(5) NOT NULL DEFAULT '10',
 `points` int(5) NOT NULL DEFAULT '10',
 `points_type` int(3) NOT NULL DEFAULT '5',
 `purchase_limit` int(2) NOT NULL DEFAULT '5',
 `catalog_item` int(11) NOT NULL,
 `vars` varchar(1024) NOT NULL DEFAULT '' COMMENT 'List of strings seperated by a ;',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

CREATE TABLE `users_target_offer_purchases` (
 `user_id` int(11) NOT NULL,
 `offer_id` int(11) NOT NULL,
 `state` int(11) NOT NULL DEFAULT '0',
 `amount` int(11) NOT NULL DEFAULT '0',
 `last_purchase` int(11) NOT NULL DEFAULT '0',
 UNIQUE KEY `use_id` (`user_id`,`offer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `permissions` ADD `cmd_promote_offer` ENUM('0','1') NOT NULL DEFAULT '0' AFTER `cmd_points`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_promote_offer', 'promoteoffer;promotetargetoffer;promote_offer'), ('commands.description.cmd_promote_offer', ':promoteoffer <offer_id> [info]');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.cmd_promote_offer.info', 'info'), ('commands.error.cmd_promote_offer.not_found', 'The offer could not be found. Use :promoteoffer info to see a list of active offers.');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.cmd_promote_offer.list', 'All available offers (%amount%):<br>%list%');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.cmd_promote_offer.list.entry', '%id%: %title% %description%');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_promote_offer', 'The promoted offer has been changed to %id%: %title%');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.wordfilter.replacement', 'bobba');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_filterword', 'filter;banword;filterword'),
                                                     ('commands.error.cmd_filterword.missing_word', 'Please specify the word to filter, with an optional replacement.'),
                                                     ('commands.error.cmd_filterword.error', 'Failed to add the word to the wordfilter. Possible duplicate?'),
                                                     ('commands.succes.cmd_filterword.added', 'Wordfilter word %word% has been added with replacement %replacement%!'),
                                                     ('commands.description.cmd_filterword', ':filter <word> [replacement]');
ALTER TABLE `permissions` ADD `cmd_filterword` ENUM('0','1') NOT NULL DEFAULT '0' AFTER `cmd_fastwalk`;

INSERT IGNORE INTO `emulator_settings` (`key`, `value`) VALUES ('debug.show.headers', '');
UPDATE `navigator_filter` SET `database_query` = 'SELECT * FROM rooms WHERE tags LIKE CONCAT(?, \";%\") ' WHERE `navigator_filter`.`key` = 'tag';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.catalog.purchase.cooldown', '3');

#DATABASE UPDATE: 1.12.0 -> 1.13.0