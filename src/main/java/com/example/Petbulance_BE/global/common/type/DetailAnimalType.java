package com.example.Petbulance_BE.global.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DetailAnimalType {
    HAMSTER("햄스터"),
    GUINEAPIG("기니피그"),
    CHINCHILLA("친칠라"),
    RABBIT("토끼"),
    HEDGEHOG("고슴도치"),
    FERRET("페럿"),
    SUGAR_GLIDER("슈가글라이더"),
    PRAIRIE_DOG("프레리도그"),
    FLYING_SQUIRREL("하늘다람쥐"),
    OTHER_SMALL_MAMMALS("기타소동물"),

    // 조류 (Avian)
    PARROT("앵무새"),
    FINCH_TYPES("핀치류"),
    OTHER_BIRDS("기타조류"),

    // 파충류 (Reptile)
    GECKO("게코"),
    OTHER_LIZARDS("기타 도마뱀"),
    SNAKE("뱀"),
    TURTLE("거북이"),
    OTHER_REPTILES("기타 파충류"),

    // 양서류 (Amphibian)
    FROG("개구리"),
    AXOLOTL("우파루파"),
    SALAMANDER("도롱뇽"),
    OTHER_AMPHIBIANS("기타 양서류"),

    // 어류 (Fish)
    ORNAMENTAL_FISH("관상어");


    private final String description;
}
