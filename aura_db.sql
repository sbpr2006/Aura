-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: demo
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `playlist_songs`
--

DROP TABLE IF EXISTS `playlist_songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist_songs` (
  `playlist_song_id` bigint NOT NULL AUTO_INCREMENT,
  `playlist_id` bigint NOT NULL,
  `song_id` bigint NOT NULL,
  PRIMARY KEY (`playlist_song_id`),
  KEY `FKqfutupgj870d2k31ldxqqwr8w` (`playlist_id`),
  KEY `FK5xu79gpgpc1p4tku7j6dv2skb` (`song_id`),
  CONSTRAINT `FK5xu79gpgpc1p4tku7j6dv2skb` FOREIGN KEY (`song_id`) REFERENCES `songs` (`song_id`),
  CONSTRAINT `FKqfutupgj870d2k31ldxqqwr8w` FOREIGN KEY (`playlist_id`) REFERENCES `playlists` (`playlist_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist_songs`
--

LOCK TABLES `playlist_songs` WRITE;
/*!40000 ALTER TABLE `playlist_songs` DISABLE KEYS */;
INSERT INTO `playlist_songs` VALUES (7,14,1),(9,14,7),(11,1,3),(12,2,3),(14,3,3),(16,1,1),(17,15,4),(18,15,1),(19,15,3);
/*!40000 ALTER TABLE `playlist_songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlists`
--

DROP TABLE IF EXISTS `playlists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlists` (
  `playlist_id` bigint NOT NULL AUTO_INCREMENT,
  `played_count` bigint DEFAULT NULL,
  `playlist_name` varchar(200) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`playlist_id`),
  KEY `FKtgjwvfg23v990xk7k0idmqbrj` (`user_id`),
  CONSTRAINT `FKtgjwvfg23v990xk7k0idmqbrj` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlists`
--

LOCK TABLES `playlists` WRITE;
/*!40000 ALTER TABLE `playlists` DISABLE KEYS */;
INSERT INTO `playlists` VALUES (1,31,'Chill Vibes',1),(2,17,'Workout Hits',1),(3,17,'Romantic Classics',1),(4,0,'Bollywood Beats',1),(5,0,'Party Anthems',1),(6,0,'Focus Mode',1),(7,0,'Travel Tunes',1),(14,34,'FavouriteList',1),(15,NULL,'FavouriteList',3);
/*!40000 ALTER TABLE `playlists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `songs`
--

DROP TABLE IF EXISTS `songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `songs` (
  `song_id` bigint NOT NULL AUTO_INCREMENT,
  `album` varchar(200) DEFAULT NULL,
  `artist` varchar(200) DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `file_path` varchar(500) DEFAULT NULL,
  `genre` varchar(100) DEFAULT NULL,
  `image_path` varchar(500) DEFAULT NULL,
  `repeated_count` bigint DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  PRIMARY KEY (`song_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `songs`
--

LOCK TABLES `songs` WRITE;
/*!40000 ALTER TABLE `songs` DISABLE KEYS */;
INSERT INTO `songs` VALUES (1,'DJ','Vijay Prakash',260,'songs/DJ Saranam Bhaje Bhaje.mp3','action','/images/dj.jpg',2,'DJ Saranam Bhaje Bhaje'),(2,'Naa Peru Surya','shekar Ravjiani',236,'songs/Lover Also Fighter Also.mp3','romance','/images/nps.jpg',4,'Lover Also Fighter Also'),(3,'Taxiwaala','Sid Sriram',275,'songs/Maate Vinadhuga.mp3','romance','/images/Taxiwaala.jpg',2,'Maate Vinadhuga'),(4,'Awaara','Unkwown',266,'songs/Arere Vaanaa.mp3','Melody','/images/Awaara.jpg',3,'Arere Vaanaa'),(7,'dhruva','amit',107,'/songs/Dhruva Dhruva.mp3','action','/dhruva.jpg',5,'dhruva dhruva');
/*!40000 ALTER TABLE `songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(150) NOT NULL,
  `bio` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'prakash@email.com','123123123','praksh1','hiii everybody','prakash 100'),(3,'bhanu@gmail.com','123123123','bhanu','kill the bio','Somula Bhanu Prakash');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-29 20:51:26
