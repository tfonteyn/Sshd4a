/*
 * Allow an arbitrary sequence of case labels.
 *
 * Copyright (C) 2006-2020 Wayne Davison
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, visit the http://fsf.org website.
 */

/* This is included multiple times, once for every segment in a switch statement.
 * This produces the next "case N:" statement in sequence. */

#if !defined CASE_N_STATE_0
#define CASE_N_STATE_0
	case 0:
#elif !defined CASE_N_STATE_1
#define CASE_N_STATE_1
	/* FALLTHROUGH */
	case 1:
#elif !defined CASE_N_STATE_2
#define CASE_N_STATE_2
	/* FALLTHROUGH */
	case 2:
#elif !defined CASE_N_STATE_3
#define CASE_N_STATE_3
	/* FALLTHROUGH */
	case 3:
#elif !defined CASE_N_STATE_4
#define CASE_N_STATE_4
	/* FALLTHROUGH */
	case 4:
#elif !defined CASE_N_STATE_5
#define CASE_N_STATE_5
	/* FALLTHROUGH */
	case 5:
#elif !defined CASE_N_STATE_6
#define CASE_N_STATE_6
	/* FALLTHROUGH */
	case 6:
#elif !defined CASE_N_STATE_7
#define CASE_N_STATE_7
	/* FALLTHROUGH */
	case 7:
#elif !defined CASE_N_STATE_8
#define CASE_N_STATE_8
	/* FALLTHROUGH */
	case 8:
#elif !defined CASE_N_STATE_9
#define CASE_N_STATE_9
	/* FALLTHROUGH */
	case 9:
#elif !defined CASE_N_STATE_10
#define CASE_N_STATE_10
	/* FALLTHROUGH */
	case 10:
#elif !defined CASE_N_STATE_11
#define CASE_N_STATE_11
	/* FALLTHROUGH */
	case 11:
#elif !defined CASE_N_STATE_12
#define CASE_N_STATE_12
	/* FALLTHROUGH */
	case 12:
#elif !defined CASE_N_STATE_13
#define CASE_N_STATE_13
	/* FALLTHROUGH */
	case 13:
#elif !defined CASE_N_STATE_14
#define CASE_N_STATE_14
	/* FALLTHROUGH */
	case 14:
#elif !defined CASE_N_STATE_15
#define CASE_N_STATE_15
	/* FALLTHROUGH */
	case 15:
#elif !defined CASE_N_STATE_16
#define CASE_N_STATE_16
	/* FALLTHROUGH */
	case 16:
#else
#error Need to add more case statements!
#endif
