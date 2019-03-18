#DATABASE UPDATE: 1.14.0 -> 1.15.0

ALTER TABLE `pet_commands_data` ADD PRIMARY KEY(`command_id`);

INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '1', '1');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '1', '2');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '1', '3');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '1', '4');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '1', '5');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '1', '6');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '1', '7');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '2', '8');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '2', '9');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '2', '10');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '2', '11');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '2', '12');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '2', '13');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '3', '14');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '3', '15');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '3', '16');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '3', '17');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '4', '18');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '4', '19');
INSERT INTO `pet_breeding_races` (`pet_type`, `rarity_level`, `breed`) VALUES ('28', '4', '20');

ALTER TABLE `pet_actions` ADD `offspring_type` INT(3) NOT NULL DEFAULT '-1' AFTER `pet_name`;

INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '1', '10', '0', '10', '1');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '2', '20', '0', '20', '10');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '3', '30', '0', '30', '30');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '4', '40', '0', '40', '60');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '5', '50', '0', '50', '120');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '6', '60', '0', '60', '240');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '7', '70', '0', '70', '600');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '8', '80', '0', '80', '1200');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '9', '90', '0', '90', '2400');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Trampolinist', 'games', '10', '100', '0', '100', '4800');

INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '1', '10', '0', '10', '1');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '2', '20', '0', '20', '10');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '3', '30', '0', '30', '30');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '4', '40', '0', '40', '60');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '5', '50', '0', '50', '120');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '6', '60', '0', '60', '240');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '7', '70', '0', '70', '600');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '8', '80', '0', '80', '1200');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '9', '90', '0', '90', '2400');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'CrossTrainer', 'games', '10', '100', '0', '100', '4800');

INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '1', '10', '0', '10', '1');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '2', '20', '0', '20', '10');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '3', '30', '0', '30', '30');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '4', '40', '0', '40', '60');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '5', '50', '0', '50', '120');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '6', '60', '0', '60', '240');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '7', '70', '0', '70', '600');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '8', '80', '0', '80', '1200');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '9', '90', '0', '90', '2400');
INSERT INTO `achievements` (`id`, `name`, `category`, `level`, `reward_amount`, `reward_type`, `points`, `progress_needed`) VALUES (NULL, 'Jogger', 'games', '10', '100', '0', '100', '4800');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.furni.gym.achievement.olympics_c16_trampoline', 'Trampolinist'), ('hotel.furni.gym.achievement.olympics_c16_crosstrainer', 'CrossTrainer'), ('hotel.furni.gym.achievement.olympics_c16_treadmill', 'Jogger');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.furni.gym.forcerot.olympics_c16_trampoline', '0'), ('hotel.furni.gym.forcerot.olympics_c16_crosstrainer', '1'), ('hotel.furni.gym.forcerot.olympics_c16_treadmill', '1');
UPDATE emulator_settings SET `key` = 'hotel.view.ltdcountdown.itemname' WHERE `key` = 'hotel.view.ltdcountdown.itename';
INSERT IGNORE INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_empty_pets', 'emptypets;empty_pets');
INSERT IGNORE INTO `emulator_texts` (`key`, `value`) VALUES ('debug.show.headers', '');

ALTER TABLE `items_crackable` ADD PRIMARY KEY(`item_id`);
ALTER TABLE `items_crackable` ADD `required_effect` INT(3) NOT NULL DEFAULT '0' AFTER `achievement_cracked`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('generic.tile.not.exists', 'Tile does not exist!');
ALTER TABLE `items` CHANGE `z` `z` DOUBLE(10,6) NOT NULL DEFAULT '0.000000';

ALTER TABLE `items_base` ADD `customparams` VARCHAR(256) NOT NULL DEFAULT '' AFTER `multiheight`;

#END DATABASE UPDATE: 1.14.0 -> 1.15.0