package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.StudentRankingDTO;
import com.devaneios.turmadeelite.entities.SchoolClass;
import com.google.cloud.firestore.Query;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.DoubleStream;

public interface RankingService {
    Page<SchoolClass> getRankeableClassesList(String studentAuthUuid,Integer size, Integer pageNumber);

    List<StudentRankingDTO> getRankingByClass(String studentAuthUuid, Long classId);
}
