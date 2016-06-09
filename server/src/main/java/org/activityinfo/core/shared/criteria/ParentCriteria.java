package org.activityinfo.core.shared.criteria;

import org.activityinfo.core.shared.Projection;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Criteria that filters on the parent's id
 */
public class ParentCriteria implements Criteria {

    public static class Parent {

        private final ResourceId parentId;
        private ResourceId restrictedBy;
        private ResourceId classId;

        public Parent(ResourceId parentId) {
            this.parentId = parentId;
        }

        public Parent(ResourceId parentId, ResourceId restrictedBy) {
            this.parentId = parentId;
            this.restrictedBy = restrictedBy;
        }

        public ResourceId getClassId() {
            return classId;
        }

        public Parent setClassId(ResourceId classId) {
            this.classId = classId;
            return this;
        }

        public ResourceId getParentId() {
            return parentId;
        }

        public ResourceId getRestrictedBy() {
            return restrictedBy;
        }

        public Parent setRestrictedBy(ResourceId restrictedBy) {
            this.restrictedBy = restrictedBy;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Parent parent = (Parent) o;

            if (parentId != null ? !parentId.equals(parent.parentId) : parent.parentId != null) return false;
            return !(restrictedBy != null ? !restrictedBy.equals(parent.restrictedBy) : parent.restrictedBy != null) && !(classId != null ? !classId.equals(parent.classId) : parent.classId != null);

        }

        @Override
        public int hashCode() {
            int result = parentId != null ? parentId.hashCode() : 0;
            result = 31 * result + (restrictedBy != null ? restrictedBy.hashCode() : 0);
            result = 31 * result + (classId != null ? classId.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Parent{" +
                    "parentId=" + parentId +
                    ", restrictedBy=" + restrictedBy +
                    ", classId=" + classId +
                    '}';
        }
    }

    private final Parent parent;

    public ParentCriteria(Parent parent) {
        this.parent = parent;
    }

    @Override
    public void accept(CriteriaVisitor visitor) {
        visitor.visitParentCriteria(this);
    }

    public boolean selectsRoot() {
        return parent.getParentId() == null;
    }

    public ResourceId getParentId() {
        return parent.getParentId();
    }

    public Parent getParent() {
        return parent;
    }

    @Override
    public boolean apply(@Nonnull FormInstance instance) {
        return Objects.equals(parent.getParentId(), instance.getOwnerId());
    }

    @Override
    public boolean apply(@Nonnull Projection projection) {
        // todo
        return true;
    }

    @Override
    public Criteria copy() {
        return new ParentCriteria(parent);
    }

    public static ParentCriteria isRoot() {
        return new ParentCriteria(null);
    }

    public static ParentCriteria isChildOf(ResourceId id) {
        return new ParentCriteria(new Parent(id));
    }

    public static ParentCriteria isChildOf(ResourceId parentId, ResourceId restrictedBy) {
        return new ParentCriteria(new Parent(parentId, restrictedBy));
    }

    public static ParentCriteria isChildOf(ResourceId parentId, ResourceId restrictedBy, ResourceId classId) {
        return new ParentCriteria(new Parent(parentId, restrictedBy).setClassId(classId));
    }
}
