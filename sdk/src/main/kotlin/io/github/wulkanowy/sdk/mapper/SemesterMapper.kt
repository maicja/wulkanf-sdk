package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Semester
import java.time.LocalDate
import io.github.wulkanowy.sdk.hebe.register.StudentInfo.Period as HebePeriod
import io.github.wulkanowy.sdk.scrapper.register.Semester as ScrapperSemester

@JvmName("mapScrapperSemesters")
internal fun List<ScrapperSemester>.mapSemesters() = map {
    Semester(
        diaryId = it.diaryId,
        kindergartenDiaryId = it.kindergartenDiaryId,
        diaryName = it.diaryName,
        schoolYear = it.schoolYear,
        semesterId = it.semesterId,
        semesterNumber = it.semesterNumber,
        start = it.start,
        end = it.end,
        classId = it.classId,
        className = it.className,
        unitId = it.unitId,
    )
}

@JvmName("mapHebeSemesters")
internal fun List<HebePeriod>.mapSemesters() = map {
    Semester(
        diaryId = it.id,
        kindergartenDiaryId = 0,
        diaryName = it.number.toString(),
        schoolYear = LocalDate.parse(it.start.date).year,
        semesterId = it.id,
        start = LocalDate.parse(it.start.date),
        semesterNumber = it.number,
        end = LocalDate.parse(it.end.date),
        classId = it.id,
        className = "",
        unitId = it.id,
    )
}
