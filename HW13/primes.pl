init(N) :- find_all_primes(2, N).

find_all_primes(S, N) :- S * S > N, !.
find_all_primes(S, N) :- not composite_table(S), S1 is S * S, flag(S1, S, N).
find_all_primes(2, N) :- find_all_primes(3, N), !.
find_all_primes(S, N) :- S1 is S + 2, find_all_primes(S1, N).

flag(Cur, Step, N) :- Cur < N, assertz(composite_table(Cur)), Next is Cur + Step, flag(Next, Step, N).

prime(N) :- N > 1, not composite_table(N).
composite(N) :- composite_table(N).

prime_divisors(N, L) :- list(L), !, make_N(N, L, 1, 2).
prime_divisors(N, L) :- integer(N), N > 0, prime_divisors(N,L,2), !.

make_N(N, [], N, _).
make_N(N, [H], F, Prev) :- H >= Prev, prime(H), F1 is F * H, make_N(N, [], F1, H), !.
make_N(N, [H | T], F, Prev) :- H >= Prev, prime(H), F1 is F * H, make_N(N, T, F1, H).

prime_divisors(1,[],_) :- !.
prime_divisors(N, [N], _) :- prime(N), !.
prime_divisors(N,[H | T], H) :- N mod H =:= 0, N1 is N / H, prime_divisors(N1, T, H), !.
prime_divisors(N, L, 2) :- prime_divisors(N, L, 3), !.
prime_divisors(N, L, F) :- F < N, F1 is F + 2, F1 * F1 =< N, prime_divisors(N, L, F1).

lcm(A, B, LCM) :- prime_divisors(A, L1), prime_divisors(B, L2), merge(L1, L2, PD), prime_divisors(LCM, PD).

merge([], Res, Res) :- !.
merge([H | T1], [H | T2], [H | Res]) :- !, merge(T1, T2, Res).
merge([H1 | T1], [H2 | T2], Res) :- H1 > H2, !, merge([H2 | T2], [H1 | T1], Res).
merge([H1 | T1], L2, [H1 | Res]) :- merge(T1, L2, Res).