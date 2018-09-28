#DATABASE UPDATE: 1.11.0 -> 1.12.0

#Texts update
INSERT INTO  `emulator_texts` (`key` ,`value`) VALUES
('scripter.warning.chat.length',  '%username% tried to send a room chat message with length %length% while the maximum length is 100 characters.');

#Update catalog_pages table.
ALTER TABLE `catalog_pages` CHANGE `page_headline` `page_headline` VARCHAR(1024) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '', CHANGE `page_teaser` `page_teaser` VARCHAR(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '', CHANGE `page_special` `page_special` VARCHAR(2048) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '', CHANGE `page_text1` `page_text1` VARCHAR(2048) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '', CHANGE `page_text2` `page_text2` VARCHAR(1024) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '', CHANGE `page_text_details` `page_text_details` VARCHAR(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '', CHANGE `page_text_teaser` `page_text_teaser` VARCHAR(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';

#Configuration Update
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.room.nooblobby', '3');              #Defines the noob lobby for NUX
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.floorplan.max.widthlength', '64');  #Maximum x/y size for the floorplan editor.
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.floorplan.max.totalarea', '4096');  #Maximum total tiles for the floor plan editor.

ALTER TABLE `bots` CHANGE `chat_lines` `chat_lines` VARCHAR(5112) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
UPDATE bots SET chat_lines = '' WHERE chat_lines LIKE 'Default Message!%';
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.max.chatlength', '120');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.max.namelength', '15');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.max.chatdelay', '604800');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.chat.max.length', '100');

UPDATE items_base SET interaction_type = 'tent' WHERE item_name LIKE '%tent%';

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_invisible', 'invisible;hideme'), ('commands.description.cmd_invisible', ':invisible');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_invisible.updated', 'You are now invisible. Reload the room to reset.');
ALTER TABLE `permissions` ADD `cmd_invisible` ENUM('0','1','2') NOT NULL DEFAULT '0' AFTER `cmd_hal`;
ALTER TABLE `permissions` ADD `acc_can_stalk` ENUM('0','1') NOT NULL DEFAULT '0' AFTER `cmd_ha`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
('commands.keys.cmd_hidewired', 'hidewired;hidemywired;wiredbegone'),
('commands.succes.cmd_hidewired.hidden', 'Wired is now hidden.'),
('commands.succes.cmd_hidewired.shown', 'Wired is now shown.'),
('commands.errors.cmd_hidewired.permission', 'You don\'t have permission to hide wireds in this room!');

ALTER TABLE `permissions` ADD `cmd_hidewired` ENUM('0','1','2') NOT NULL DEFAULT '2' AFTER `cmd_happyhour`;

ALTER TABLE `rooms` ADD `hidewired` ENUM('0','1') NOT NULL DEFAULT '0' AFTER `jukebox_active`;

UPDATE `emulator_texts` SET `value` = ':masspoints <amount> [type] ' WHERE `emulator_texts`.`key` = 'commands.description.cmd_masspoints';

#END DATABASE UPDATE: 1.11.0 -> 1.12.0