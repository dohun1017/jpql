package jpql.main;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain7 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            /**
             * 페치 조인 대상에는 별칭을 줄 수 없다.
             *      하이버네이트는 가능하지만 가급적이면 사용하지 말 것.
             * 둘 이상의 컬렉션은 페치 조인 할 수 없다.
             * 컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다.
             *      일대일, 대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능
             *      하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우 위험)
             *
             * @BatchSize(size = 100) - 배치사이즈는 1000이하 알아서 잘 설정하기.
             *      in 쿼리를 100개씩 보냄
             * persistence.xml 글로벌 설정 가능
             *      <property name="hibernate.default_batch_fetch_size" value="100"/>
             */
            em.createQuery("select t from Team t", Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList()
                    .forEach(t -> {
                        List<Member> members = t.getMembers();
                        for (Member member : members) {
                            System.out.println("member.getUsername() = " + member.getUsername());
                        }
                        System.out.println("t.getName() = " + t.getName());
                    });

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}