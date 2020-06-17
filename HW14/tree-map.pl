map_build([], null) :- !.
map_build([(Key, Value) | T], TreeMap) :- map_build(T, Res), map_put(Res, Key, Value, TreeMap).

update(null, null) :- !.
update(node(Key, Value, Priority, _, null, null),
				node(Key, Value, Priority, 1, null, null)) :- !.
update(node(Key, Value, Priority, _, null, node(Key2, Value2, Priority2, C2, Left2, Right2)),
				node(Key, Value, Priority, C3, null, node(Key2, Value2, Priority2, C2, Left2, Right2))) :- C3 is C2 + 1, !.
update(node(Key, Value, Priority, _, node(Key1, Value1, Priority1, C1, Left1, Right1), null),
				node(Key, Value, Priority, C3, node(Key1, Value1, Priority1, C1, Left1, Right1), null)) :- C3 is C1 + 1, !.
update(node(Key, Value, Priority, _, node(Key1, Value1, Priority1, C1, Left1, Right1),
				node(Key2, Value2, Priority2, C2, Left2, Right2)),
				node(Key, Value, Priority, C3, node(Key1, Value1, Priority1, C1, Left1, Right1),
				node(Key2, Value2, Priority2, C2, Left2, Right2))) :- C3 is C1 + C2 + 1.

split(null, _, null, null) :- !.
split(node(Key, Value, Priority, C, Left, Right), K, L, R) :-
				K > Key, !,
				split(Right, K, Right1, R),
				update(node(Key, Value, Priority, C, Left, Right1), L).
split(node(Key, Value, Priority, C, Left, Right), K, L, R) :-
				split(Left, K, L, Left1),
				update(node(Key, Value, Priority, C, Left1, Right), R).

merge(null, Treap, Treap) :- !.
merge(Treap, null, Treap) :- !.
merge(node(Key1, Value1, Priority1, C1, Left1, Right1),
				node(Key2, Value2, Priority2, C2, Left2, Right2), Result) :- Priority1 > Priority2, !,
				merge(Right1, node(Key2, Value2, Priority2, C2, Left2, Right2), Right),
				update(node(Key1, Value1, Priority1, C1, Left1, Right), Result).
merge(node(Key1, Value1, Priority1, C1, Left1, Right1),
				node(Key2, Value2, Priority2, C2, Left2, Right2), Result) :-
				merge(node(Key1, Value1, Priority1, C1, Left1, Right1), Left2, Left),
				update(node(Key2, Value2, Priority2, C2, Left, Right2), Result).

map_put(null, Key, Value, node(Key, Value, Priority, 1, null, null)) :-
				rand_int(2147483647, Priority).
map_put(node(Key, Value, Priority, C, Left, Right), Key1, Value1, Result) :-
				map_get(node(Key, Value, Priority, C, Left, Right), Key1, _), !,
				split(node(Key, Value, Priority, C, Left, Right), Key1, T1, T2),
				map_change(T2, Value1, T), merge(T1, T, Result).
map_put(node(Key, Value, Priority, C, Left, Right), Key1, Value1, Result) :-
				split(node(Key, Value, Priority, C, Left, Right), Key1, T1, T2),
				rand_int(2147483647, Priority1),
				merge(T1, node(Key1, Value1, Priority1, 1, null, null), T), merge(T, T2, Result).

map_change(node(Key, _, Priority, C, null, Right), Value1,
				node(Key, Value1, Priority, C, null, Right)) :- !.
map_change(node(Key, Value, Priority, C, Left, Right), Value1,
				node(Key, Value, Priority, C, Left1, Right)) :-
				map_change(Left, Value1, Left1).

map_remove(node(Key, Value, Priority, C, Left, Right), Key1, Result) :-
				map_get(node(Key, Value, Priority, C, Left, Right), Key1, _), !,
				split(node(Key, Value, Priority, C, Left, Right), Key1, T1, T2),
				remove(T2, T), merge(T1, T, Result).
map_remove(Treap, _, Treap).

remove(node(_, _, _, _, null, Right), Right) :- !.
remove(node(Key, Value, Priority, C, node(_, _, _, null, Right1), Right), node(Key, Value, Priority, C1, Right1, Right)) :-
				C1 is C - 1, !.
remove(node(Key, Value, Priority, C, Left, Right), node(Key, Value, Priority, C1, Left1, Right)) :-
				remove(Left, Left1), C1 is C - 1.

map_get(node(Key, Value, _, _, _, _), Key, Value) :- !.
map_get(node(Key, _, _, _, _, Right), Key1, Value1) :-
				Key1 > Key, !, map_get(Right, Key1, Value1).
map_get(node(_, _, _, _, Left, _), Key1, Value1) :-
				map_get(Left, Key1, Value1).

map_submapSize(Treap, FromKey, ToKey, Size) :-
				split(Treap, ToKey, T1, _),
				split(T1, FromKey, _, node(_, _, _, Size, _, _)).
map_submapSize(Treap, FromKey, ToKey, 0) :-
				split(Treap, ToKey, T1, _),
				split(T1, FromKey, _, null).