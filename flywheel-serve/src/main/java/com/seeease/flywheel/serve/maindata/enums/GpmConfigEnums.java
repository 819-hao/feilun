package com.seeease.flywheel.serve.maindata.enums;

public enum GpmConfigEnums {

    ;

    public enum ToTarget {

        ToC,
        ToB,
        ;

        ToTarget() {
        }

        public static ToTarget findByToTarget(String toTarget) {

            for (ToTarget value : values()) {
                if (value.name().equals(toTarget)) {
                    return value;
                }
            }
            return null;
        }

    }

}
