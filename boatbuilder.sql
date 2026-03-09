-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: boatbuilder
-- ------------------------------------------------------
-- Server version	8.0.45-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `boat`
--

DROP TABLE IF EXISTS `boat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `boat` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `ownerUUID` varchar(250) NOT NULL COMMENT 'The UUID of the player owning the boat',
  `isLive` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 if the boat is in edit mode, a 1 if the boat is live',
  `x` int NOT NULL COMMENT 'The x coordinate of the boat''s origin position',
  `y` int NOT NULL COMMENT 'The y coordinate of the boat''s origin position',
  `z` int NOT NULL COMMENT 'The z coordinate of the boat''s origin position',
  `inEditMode` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `boat`
--

LOCK TABLES `boat` WRITE;
/*!40000 ALTER TABLE `boat` DISABLE KEYS */;
/*!40000 ALTER TABLE `boat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `boat_block`
--

DROP TABLE IF EXISTS `boat_block`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `boat_block` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `isCenter` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'If this block is the center block of a boat',
  `x` int NOT NULL COMMENT 'The x coordinate of the block of a boat',
  `y` int NOT NULL COMMENT 'The y coordinate of the block of a boat',
  `z` int NOT NULL COMMENT 'The z coordinate of the block of a boat',
  `boatID` int NOT NULL COMMENT 'The ID of the boat this block is connected to',
  PRIMARY KEY (`ID`),
  KEY `BoatFK_idx` (`boatID`),
  CONSTRAINT `BoatFK` FOREIGN KEY (`boatID`) REFERENCES `boat` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `boat_block`
--

LOCK TABLES `boat_block` WRITE;
/*!40000 ALTER TABLE `boat_block` DISABLE KEYS */;
/*!40000 ALTER TABLE `boat_block` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'boatbuilder'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-07 22:36:02
