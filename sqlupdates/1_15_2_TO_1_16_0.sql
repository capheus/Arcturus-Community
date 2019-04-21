#DATABASE UPDATE: 1.15.2 -> 1.16.0

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.room.rollers.norules', '0');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('postit.charlimit', '366');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.max.friends.hc', '300');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.navigator.populartab.publics', '0');

ALTER TABLE `items_base` CHANGE `stack_height` `stack_height` DOUBLE(4,2) NOT NULL DEFAULT '0.00';

ALTER TABLE `permissions` ADD `badge` VARCHAR(12) NOT NULL DEFAULT '' AFTER `rank_name`;
ALTER TABLE `permissions` ADD `acc_ads_background` ENUM('0','1') NOT NULL DEFAULT '0' AFTER `acc_camera`;
ALTER TABLE `permissions` ADD `cmd_update_achievements` ENUM('0','1') NOT NULL DEFAULT '0' AFTER `cmd_unmute`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.errors.cmd_give_rank.not_found', 'Rank %id% could not be given to %username% as it does not exist!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('hotel.wired.giveachievement.invalid.points', 'This is not a number!'), ('hotel.wired.giveachievement.invalid.achievement', '%achievement% does not exist!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('hotel.error.roomads.nopermission', 'You have no permission to modify room ads!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_update_achievements', 'uach;update_achievements'), ('commands.description.cmd_update_achievements', ':update_achievements');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_update_achievements.updated', 'Achievements have been reloaded!');
#END DATABASE UPDATE: 1.15.2 -> 1.16.0