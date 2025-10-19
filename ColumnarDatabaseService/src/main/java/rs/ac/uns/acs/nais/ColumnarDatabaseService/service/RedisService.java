package rs.ac.uns.acs.nais.ColumnarDatabaseService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.StudentGrade;
import rs.ac.uns.acs.nais.ColumnarDatabaseService.entity.SubjectStatistics;

import java.util.concurrent.TimeUnit;

/**
 * Redis Service za keširanje podataka
 */
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Ažuriraj keš statistika predmeta
     */
    public void updateSubjectStatisticsCache(SubjectStatistics statistics) {
        try {
            String key = "subject_stats:" + statistics.getDepartment() + ":" + 
                        statistics.getAcademicYear() + ":" + statistics.getSubjectId();
            redisTemplate.opsForValue().set(key, statistics, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            // Log greške ali ne prekidaj transakciju
            System.err.println("Redis cache update failed for subject statistics: " + e.getMessage());
        }
    }

    /**
     * Ažuriraj keš ocena studenta
     */
    public void updateStudentGradesCache(Long studentId, StudentGrade grade) {
        try {
            String key = "student_grades:" + studentId;
            redisTemplate.opsForValue().set(key, grade, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            System.err.println("Redis cache update failed for student grades: " + e.getMessage());
        }
    }

    /**
     * Invalidate cache
     */
    public void invalidateCache(String keyPattern) {
        try {
            redisTemplate.delete(keyPattern);
        } catch (Exception e) {
            System.err.println("Redis cache invalidation failed: " + e.getMessage());
        }
    }

    /**
     * Test da li je Redis dostupan
     */
    public boolean isRedisAvailable() {
        try {
            redisTemplate.opsForValue().set("test", "test", 1, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}