package mods.thecomputerizer.discord.serverstalker.util;

import reactor.core.publisher.Mono;

import java.util.Objects;

public class MonoHelper {
    
    public static Mono<Void> combine(Mono<Void> mono1, Mono<Void> mono2) {
        return mono1.then().and(mono2.then());
    }
    
    @SuppressWarnings("unchecked")
    public static Mono<Void> combineAll(Mono<?> ... monos) {
        if(Objects.isNull(monos) || monos.length==0) return Mono.empty();
        Mono<Void> mono = (Mono<Void>)monos[0];
        for(int i=1;i<monos.length;i++) mono = combine(mono,(Mono<Void>)monos[i]);
        return mono;
    }
}
