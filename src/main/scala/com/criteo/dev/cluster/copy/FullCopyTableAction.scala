package com.criteo.dev.cluster.copy

import com.criteo.dev.cluster._
import com.criteo.dev.cluster.config.GlobalConfig
import org.slf4j.LoggerFactory

/**
  * Copy over all table data.
  */
class FullCopyTableAction(config: GlobalConfig, conf: Map[String, String], source: Node, target: Node) {

  private val logger = LoggerFactory.getLogger(classOf[FullCopyTableAction])

  def copy(tableInfo: TableInfo): TableInfo = {
    val database = tableInfo.database
    val table = tableInfo.ddl.table
    val location = tableInfo.ddl.location.get
    val partitions = tableInfo.partitions

    logger.info("Copying " + partitions.length + " partitions from " +
      database + "." + table)

    //for now, only support table location as the common location.
    val sourceCommon = CopyUtilities.getCommonLocation(location, partitions)

    //handle case of no partitions, and some partitions.
    val sourceLocations: Array[String] = {
      if (partitions.isEmpty) Array(location)
      else partitions.map(_.location)
    }

    val copyFileAction = CopyFileActionFactory.getCopyFileAction(config, source, target)
    copyFileAction(sourceLocations, sourceCommon, CopyUtilities.toRelative(sourceCommon))
    tableInfo
  }

//  def getCommonLocation(partLocation: Array[String]): String = {
//    val location = partLocation.reduce[String] { case (prefix, cur) =>
//      prefix.zip(cur).takeWhile { case (a, b) => a == b }.map(_._1).mkString
//    }
//
//    CopyUtilities.getParent(location)
//  }
}