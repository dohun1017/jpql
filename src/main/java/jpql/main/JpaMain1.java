package jpql.main;

import jpql.Member;

import javax.persistence.*;

public class JpaMain1 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            /**
             * TypedQuery 반환 타입이 명확할 때 사용
             * Query 반환 타입이 명확하지 않을 때 사용
             */
//            TypedQuery<Member> query1 = em.createQuery("select m from Member as m", Member.class);
//            TypedQuery<String> query2 = em.createQuery("select m.username from Member as m", String.class);
//            Query query3 = em.createQuery("select m.username, m.age from Member as m");

            /**
             * .getResultList(): 다건 조회
             *      - 결과가 없으면 빈 리스트 반환(NullPointerException 걱정 필요 X)
             * .getSingleResult(): 단건 조회(무조건 결과가 하나 있을때만 사용)
             *      - 결과가 없으면 NoResultException
             *      - 둘 이상이면 NonUniqueResultException
             */
//            List<Member> resultList = query1.getResultList();
//            Member singleResult = query1.getSingleResult();

            em.createQuery("select m from Member as m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getResultList().forEach(m -> System.out.println("member.username() = " + m.getUsername()));

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