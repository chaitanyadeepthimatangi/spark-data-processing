package com.proofpoint.services

import com.proofpoint.domain.dao.SidActivityDAO.SidActivity
import com.proofpoint.domain.dao.SidActivityDaoImpl


trait SidActivityService {
  def save(sidActivity: Seq[SidActivity])
}

class SidActivityServiceImpl(repo: SidActivityDaoImpl) extends SidActivityService {
  def save(activitySeq: Seq[SidActivity]) = {
    repo.insertSidActivity(activitySeq)
  }
}
