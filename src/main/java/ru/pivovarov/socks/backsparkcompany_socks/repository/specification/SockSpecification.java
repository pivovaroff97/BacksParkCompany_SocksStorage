package ru.pivovarov.socks.backsparkcompany_socks.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.pivovarov.socks.backsparkcompany_socks.dto.SockSearchCriteria;
import ru.pivovarov.socks.backsparkcompany_socks.model.Sock;

import javax.persistence.criteria.Predicate;

public class SockSpecification {

    public static Specification<Sock> filterByParams(SockSearchCriteria criteria) {
        if (criteria == null) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (criteria.getColor() != null) {
                predicate = criteriaBuilder.equal(root.get("color"), criteria.getColor());
            }
            if (criteria.getOperation() != null
                    && criteria.getOperation().equalsIgnoreCase(OperationType.BETWEEN.getValue())
                    && criteria.getMinCotton() != null && criteria.getMaxCotton() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.between(
                                root.get("cottonPercent"), criteria.getMinCotton(), criteria.getMaxCotton()));
            } else if (criteria.getOperation() != null && criteria.getCotton() != null) {
                if (criteria.getOperation().equalsIgnoreCase(OperationType.MORE_THAN.getValue())) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.greaterThan(root.get("cottonPercent"), criteria.getCotton()));
                }
                if (criteria.getOperation().equalsIgnoreCase(OperationType.LESS_THAN.getValue())) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.lessThan(root.get("cottonPercent"), criteria.getCotton()));
                }
                if (criteria.getOperation().equalsIgnoreCase(OperationType.EQUAL.getValue())) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.equal(root.get("cottonPercent"), criteria.getCotton()));
                }
            }
            return predicate;
        });
    }
}
