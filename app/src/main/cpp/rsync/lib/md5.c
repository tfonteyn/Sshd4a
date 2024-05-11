/*
 * RFC 1321 compliant MD5 implementation
 *
 * Copyright (C) 2001-2003 Christophe Devine
 * Copyright (C) 2007-2022 Wayne Davison
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

#include "rsync.h"

void md5_begin(md_context *ctx)
{
	ctx->A = 0x67452301;
	ctx->B = 0xEFCDAB89;
	ctx->C = 0x98BADCFE;
	ctx->D = 0x10325476;

	ctx->totalN = ctx->totalN2 = 0;
}

static void md5_process(md_context *ctx, const uchar data[CSUM_CHUNK])
{
	uint32 X[16], A, B, C, D;

	A = ctx->A;
	B = ctx->B;
	C = ctx->C;
	D = ctx->D;

	X[0] = IVALu(data, 0);
	X[1] = IVALu(data, 4);
	X[2] = IVALu(data, 8);
	X[3] = IVALu(data, 12);
	X[4] = IVALu(data, 16);
	X[5] = IVALu(data, 20);
	X[6] = IVALu(data, 24);
	X[7] = IVALu(data, 28);
	X[8] = IVALu(data, 32);
	X[9] = IVALu(data, 36);
	X[10] = IVALu(data, 40);
	X[11] = IVALu(data, 44);
	X[12] = IVALu(data, 48);
	X[13] = IVALu(data, 52);
	X[14] = IVALu(data, 56);
	X[15] = IVALu(data, 60);

#define S(x,n) ((x << n) | ((x & 0xFFFFFFFF) >> (32 - n)))

#define P(a,b,c,d,k,s,t) a += F(b,c,d) + X[k] + t, a = S(a,s) + b

#define F(x,y,z) (z ^ (x & (y ^ z)))

	P(A, B, C, D,  0,  7, 0xD76AA478);
	P(D, A, B, C,  1, 12, 0xE8C7B756);
	P(C, D, A, B,  2, 17, 0x242070DB);
	P(B, C, D, A,  3, 22, 0xC1BDCEEE);
	P(A, B, C, D,  4,  7, 0xF57C0FAF);
	P(D, A, B, C,  5, 12, 0x4787C62A);
	P(C, D, A, B,  6, 17, 0xA8304613);
	P(B, C, D, A,  7, 22, 0xFD469501);
	P(A, B, C, D,  8,  7, 0x698098D8);
	P(D, A, B, C,  9, 12, 0x8B44F7AF);
	P(C, D, A, B, 10, 17, 0xFFFF5BB1);
	P(B, C, D, A, 11, 22, 0x895CD7BE);
	P(A, B, C, D, 12,  7, 0x6B901122);
	P(D, A, B, C, 13, 12, 0xFD987193);
	P(C, D, A, B, 14, 17, 0xA679438E);
	P(B, C, D, A, 15, 22, 0x49B40821);

#undef F
#define F(x,y,z) (y ^ (z & (x ^ y)))

	P(A, B, C, D,  1,  5, 0xF61E2562);
	P(D, A, B, C,  6,  9, 0xC040B340);
	P(C, D, A, B, 11, 14, 0x265E5A51);
	P(B, C, D, A,  0, 20, 0xE9B6C7AA);
	P(A, B, C, D,  5,  5, 0xD62F105D);
	P(D, A, B, C, 10,  9, 0x02441453);
	P(C, D, A, B, 15, 14, 0xD8A1E681);
	P(B, C, D, A,  4, 20, 0xE7D3FBC8);
	P(A, B, C, D,  9,  5, 0x21E1CDE6);
	P(D, A, B, C, 14,  9, 0xC33707D6);
	P(C, D, A, B,  3, 14, 0xF4D50D87);
	P(B, C, D, A,  8, 20, 0x455A14ED);
	P(A, B, C, D, 13,  5, 0xA9E3E905);
	P(D, A, B, C,  2,  9, 0xFCEFA3F8);
	P(C, D, A, B,  7, 14, 0x676F02D9);
	P(B, C, D, A, 12, 20, 0x8D2A4C8A);

#undef F
#define F(x,y,z) (x ^ y ^ z)

	P(A, B, C, D,  5,  4, 0xFFFA3942);
	P(D, A, B, C,  8, 11, 0x8771F681);
	P(C, D, A, B, 11, 16, 0x6D9D6122);
	P(B, C, D, A, 14, 23, 0xFDE5380C);
	P(A, B, C, D,  1,  4, 0xA4BEEA44);
	P(D, A, B, C,  4, 11, 0x4BDECFA9);
	P(C, D, A, B,  7, 16, 0xF6BB4B60);
	P(B, C, D, A, 10, 23, 0xBEBFBC70);
	P(A, B, C, D, 13,  4, 0x289B7EC6);
	P(D, A, B, C,  0, 11, 0xEAA127FA);
	P(C, D, A, B,  3, 16, 0xD4EF3085);
	P(B, C, D, A,  6, 23, 0x04881D05);
	P(A, B, C, D,  9,  4, 0xD9D4D039);
	P(D, A, B, C, 12, 11, 0xE6DB99E5);
	P(C, D, A, B, 15, 16, 0x1FA27CF8);
	P(B, C, D, A,  2, 23, 0xC4AC5665);

#undef F
#define F(x,y,z) (y ^ (x | ~z))

	P(A, B, C, D,  0,  6, 0xF4292244);
	P(D, A, B, C,  7, 10, 0x432AFF97);
	P(C, D, A, B, 14, 15, 0xAB9423A7);
	P(B, C, D, A,  5, 21, 0xFC93A039);
	P(A, B, C, D, 12,  6, 0x655B59C3);
	P(D, A, B, C,  3, 10, 0x8F0CCC92);
	P(C, D, A, B, 10, 15, 0xFFEFF47D);
	P(B, C, D, A,  1, 21, 0x85845DD1);
	P(A, B, C, D,  8,  6, 0x6FA87E4F);
	P(D, A, B, C, 15, 10, 0xFE2CE6E0);
	P(C, D, A, B,  6, 15, 0xA3014314);
	P(B, C, D, A, 13, 21, 0x4E0811A1);
	P(A, B, C, D,  4,  6, 0xF7537E82);
	P(D, A, B, C, 11, 10, 0xBD3AF235);
	P(C, D, A, B,  2, 15, 0x2AD7D2BB);
	P(B, C, D, A,  9, 21, 0xEB86D391);

#undef F

	ctx->A += A;
	ctx->B += B;
	ctx->C += C;
	ctx->D += D;
}

#ifdef USE_MD5_ASM
#if CSUM_CHUNK != 64
#error The MD5 ASM code does not support CSUM_CHUNK != 64
#endif
extern void md5_process_asm(md_context *ctx, const void *data, size_t num);
#endif

void md5_update(md_context *ctx, const uchar *input, uint32 length)
{
	uint32 left, fill;

	if (!length)
		return;

	left = ctx->totalN & 0x3F;
	fill = CSUM_CHUNK - left;

	ctx->totalN += length;
	ctx->totalN &= 0xFFFFFFFF;

	if (ctx->totalN < length)
		ctx->totalN2++;

	if (left && length >= fill) {
		memcpy(ctx->buffer + left, input, fill);
		md5_process(ctx, ctx->buffer);
		length -= fill;
		input  += fill;
		left = 0;
	}

#ifdef USE_MD5_ASM /* { */
	if (length >= CSUM_CHUNK) {
		uint32 chunks = length / CSUM_CHUNK;
		md5_process_asm(ctx, input, chunks);
		length -= chunks * CSUM_CHUNK;
		input += chunks * CSUM_CHUNK;
	}
#else /* } { */
	while (length >= CSUM_CHUNK) {
		md5_process(ctx, input);
		length -= CSUM_CHUNK;
		input  += CSUM_CHUNK;
	}
#endif /* } */

	if (length)
		memcpy(ctx->buffer + left, input, length);
}

static uchar md5_padding[CSUM_CHUNK] = { 0x80 };

void md5_result(md_context *ctx, uchar digest[MD5_DIGEST_LEN])
{
	uint32 last, padn;
	uint32 high, low;
	uchar msglen[8];

	high = (ctx->totalN >> 29)
	     | (ctx->totalN2 <<  3);
	low  = (ctx->totalN <<  3);

	SIVALu(msglen, 0, low);
	SIVALu(msglen, 4, high);

	last = ctx->totalN & 0x3F;
	padn = last < 56 ? 56 - last : 120 - last;

	md5_update(ctx, md5_padding, padn);
	md5_update(ctx, msglen, 8);

	SIVALu(digest, 0, ctx->A);
	SIVALu(digest, 4, ctx->B);
	SIVALu(digest, 8, ctx->C);
	SIVALu(digest, 12, ctx->D);
}

#ifdef TEST_MD5 /* { */

void get_md5(uchar *out, const uchar *input, int n)
{
	md_context ctx;
	md5_begin(&ctx);
	md5_update(&ctx, input, n);
	md5_result(&ctx, out);
}

#include <stdlib.h>
#include <stdio.h>

/*
 * those are the standard RFC 1321 test vectors
 */

static struct {
    char *str, *md5;
} tests[] = {
 { "",
   "d41d8cd98f00b204e9800998ecf8427e" },
 { "a",
   "0cc175b9c0f1b6a831c399e269772661" },
 { "abc",
   "900150983cd24fb0d6963f7d28e17f72" },
 { "message digest",
   "f96b697d7cb7938d525a2f31aaf161d0" },
 { "abcdefghijklmnopqrstuvwxyz",
   "c3fcd3d76192e4007dfb496cca67e13b" },
 { "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
   "d174ab98d277d9f5a5611c2c9f419d9f" },
 { "12345678901234567890123456789012345678901234567890123456789012345678901234567890",
   "57edf4a22be3c955ac49da2e2107b67a" },
 { NULL, NULL }
};

int main(int argc, char *argv[])
{
	FILE *f;
	int i, j;
	char output[33];
	md_context ctx;
	uchar buf[1000];
	uchar md5sum[MD5_DIGEST_LEN];

	if (argc < 2) {
		printf("\nMD5 Validation Tests:\n\n");

		for (i = 0; tests[i].str; i++) {
			char *str = tests[i].str;
			char *chk = tests[i].md5;

			printf("  Test %d ", i + 1);

			get_md5(md5sum, str, strlen(str));

			for (j = 0; j < MD5_DIGEST_LEN; j++)
				sprintf(output + j * 2, "%02x", md5sum[j]);

			if (memcmp(output, chk, 32)) {
				printf("failed!\n");
				return 1;
			}

			printf("passed.\n");
		}

		printf("\n");
		return 0;
	}

	while (--argc) {
		if (!(f = fopen(*++argv, "rb"))) {
			perror("fopen");
			return 1;
		}

		md5_begin(&ctx);

		while ((i = fread(buf, 1, sizeof buf, f)) > 0)
			md5_update(&ctx, buf, i);

		md5_result(&ctx, md5sum);

		for (j = 0; j < MD5_DIGEST_LEN; j++)
			printf("%02x", md5sum[j]);

		printf("  %s\n", *argv);
	}

	return 0;
}

#endif /* } */
