cmake_minimum_required(VERSION 3.22.1)

project(Sshd4a)

# #############################################################################
# define simple variables with the sources grouped purely for easier editing
#
# - tommath/tomcrypt has (nearly) all files listed whether we use them or not.
# - dropbear, openssh, rsync only(?) lists the files we actually need.
# TODO: prune unneeded files

set(SRC_TOM_MATH
        src/main/cpp/dropbear/libtommath/bn_cutoffs.c
        # src/main/cpp/dropbear/libtommath/bn_deprecated.c
        src/main/cpp/dropbear/libtommath/bn_mp_2expt.c
        src/main/cpp/dropbear/libtommath/bn_mp_abs.c
        src/main/cpp/dropbear/libtommath/bn_mp_add.c
        src/main/cpp/dropbear/libtommath/bn_mp_add_d.c
        src/main/cpp/dropbear/libtommath/bn_mp_addmod.c
        src/main/cpp/dropbear/libtommath/bn_mp_and.c
        src/main/cpp/dropbear/libtommath/bn_mp_clamp.c
        src/main/cpp/dropbear/libtommath/bn_mp_clear.c
        src/main/cpp/dropbear/libtommath/bn_mp_clear_multi.c
        src/main/cpp/dropbear/libtommath/bn_mp_cmp.c
        src/main/cpp/dropbear/libtommath/bn_mp_cmp_d.c
        src/main/cpp/dropbear/libtommath/bn_mp_cmp_mag.c
        src/main/cpp/dropbear/libtommath/bn_mp_cnt_lsb.c
        src/main/cpp/dropbear/libtommath/bn_mp_complement.c
        src/main/cpp/dropbear/libtommath/bn_mp_copy.c
        src/main/cpp/dropbear/libtommath/bn_mp_count_bits.c
        src/main/cpp/dropbear/libtommath/bn_mp_decr.c
        src/main/cpp/dropbear/libtommath/bn_mp_div.c
        src/main/cpp/dropbear/libtommath/bn_mp_div_2.c
        src/main/cpp/dropbear/libtommath/bn_mp_div_2d.c
        src/main/cpp/dropbear/libtommath/bn_mp_div_3.c
        src/main/cpp/dropbear/libtommath/bn_mp_div_d.c
        src/main/cpp/dropbear/libtommath/bn_mp_dr_is_modulus.c
        src/main/cpp/dropbear/libtommath/bn_mp_dr_reduce.c
        src/main/cpp/dropbear/libtommath/bn_mp_dr_setup.c
        src/main/cpp/dropbear/libtommath/bn_mp_error_to_string.c
        src/main/cpp/dropbear/libtommath/bn_mp_exch.c
        src/main/cpp/dropbear/libtommath/bn_mp_expt_u32.c
        src/main/cpp/dropbear/libtommath/bn_mp_exptmod.c
        src/main/cpp/dropbear/libtommath/bn_mp_exteuclid.c
        src/main/cpp/dropbear/libtommath/bn_mp_fread.c
        src/main/cpp/dropbear/libtommath/bn_mp_from_sbin.c
        src/main/cpp/dropbear/libtommath/bn_mp_from_ubin.c
        src/main/cpp/dropbear/libtommath/bn_mp_fwrite.c
        src/main/cpp/dropbear/libtommath/bn_mp_gcd.c
        src/main/cpp/dropbear/libtommath/bn_mp_get_i32.c
        src/main/cpp/dropbear/libtommath/bn_mp_get_i64.c
        src/main/cpp/dropbear/libtommath/bn_mp_get_l.c
        src/main/cpp/dropbear/libtommath/bn_mp_get_ll.c
        src/main/cpp/dropbear/libtommath/bn_mp_get_mag_u32.c
        src/main/cpp/dropbear/libtommath/bn_mp_get_mag_u64.c
        src/main/cpp/dropbear/libtommath/bn_mp_get_mag_ul.c
        src/main/cpp/dropbear/libtommath/bn_mp_get_mag_ull.c
        src/main/cpp/dropbear/libtommath/bn_mp_grow.c
        src/main/cpp/dropbear/libtommath/bn_mp_incr.c
        src/main/cpp/dropbear/libtommath/bn_mp_init.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_copy.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_i32.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_i64.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_l.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_ll.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_multi.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_set.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_size.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_u32.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_u64.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_ul.c
        src/main/cpp/dropbear/libtommath/bn_mp_init_ull.c
        src/main/cpp/dropbear/libtommath/bn_mp_invmod.c
        src/main/cpp/dropbear/libtommath/bn_mp_is_square.c
        src/main/cpp/dropbear/libtommath/bn_mp_iseven.c
        src/main/cpp/dropbear/libtommath/bn_mp_isodd.c
        src/main/cpp/dropbear/libtommath/bn_mp_kronecker.c
        src/main/cpp/dropbear/libtommath/bn_mp_lcm.c
        src/main/cpp/dropbear/libtommath/bn_mp_log_u32.c
        src/main/cpp/dropbear/libtommath/bn_mp_lshd.c
        src/main/cpp/dropbear/libtommath/bn_mp_mod.c
        src/main/cpp/dropbear/libtommath/bn_mp_mod_2d.c
        src/main/cpp/dropbear/libtommath/bn_mp_mod_d.c
        src/main/cpp/dropbear/libtommath/bn_mp_montgomery_calc_normalization.c
        src/main/cpp/dropbear/libtommath/bn_mp_montgomery_reduce.c
        src/main/cpp/dropbear/libtommath/bn_mp_montgomery_setup.c
        src/main/cpp/dropbear/libtommath/bn_mp_mul.c
        src/main/cpp/dropbear/libtommath/bn_mp_mul_2.c
        src/main/cpp/dropbear/libtommath/bn_mp_mul_2d.c
        src/main/cpp/dropbear/libtommath/bn_mp_mul_d.c
        src/main/cpp/dropbear/libtommath/bn_mp_mulmod.c
        src/main/cpp/dropbear/libtommath/bn_mp_neg.c
        src/main/cpp/dropbear/libtommath/bn_mp_or.c
        src/main/cpp/dropbear/libtommath/bn_mp_pack.c
        src/main/cpp/dropbear/libtommath/bn_mp_pack_count.c
        src/main/cpp/dropbear/libtommath/bn_mp_prime_fermat.c
        src/main/cpp/dropbear/libtommath/bn_mp_prime_frobenius_underwood.c
        src/main/cpp/dropbear/libtommath/bn_mp_prime_is_prime.c
        src/main/cpp/dropbear/libtommath/bn_mp_prime_miller_rabin.c
        src/main/cpp/dropbear/libtommath/bn_mp_prime_next_prime.c
        src/main/cpp/dropbear/libtommath/bn_mp_prime_rabin_miller_trials.c
        src/main/cpp/dropbear/libtommath/bn_mp_prime_rand.c
        src/main/cpp/dropbear/libtommath/bn_mp_prime_strong_lucas_selfridge.c
        src/main/cpp/dropbear/libtommath/bn_mp_radix_size.c
        src/main/cpp/dropbear/libtommath/bn_mp_radix_smap.c
        src/main/cpp/dropbear/libtommath/bn_mp_rand.c
        src/main/cpp/dropbear/libtommath/bn_mp_read_radix.c
        src/main/cpp/dropbear/libtommath/bn_mp_reduce.c
        src/main/cpp/dropbear/libtommath/bn_mp_reduce_2k.c
        src/main/cpp/dropbear/libtommath/bn_mp_reduce_2k_l.c
        src/main/cpp/dropbear/libtommath/bn_mp_reduce_2k_setup.c
        src/main/cpp/dropbear/libtommath/bn_mp_reduce_2k_setup_l.c
        src/main/cpp/dropbear/libtommath/bn_mp_reduce_is_2k.c
        src/main/cpp/dropbear/libtommath/bn_mp_reduce_is_2k_l.c
        src/main/cpp/dropbear/libtommath/bn_mp_reduce_setup.c
        src/main/cpp/dropbear/libtommath/bn_mp_root_u32.c
        src/main/cpp/dropbear/libtommath/bn_mp_rshd.c
        src/main/cpp/dropbear/libtommath/bn_mp_sbin_size.c
        src/main/cpp/dropbear/libtommath/bn_mp_set.c
        src/main/cpp/dropbear/libtommath/bn_mp_set_i32.c
        src/main/cpp/dropbear/libtommath/bn_mp_set_i64.c
        src/main/cpp/dropbear/libtommath/bn_mp_set_l.c
        src/main/cpp/dropbear/libtommath/bn_mp_set_ll.c
        src/main/cpp/dropbear/libtommath/bn_mp_set_u32.c
        src/main/cpp/dropbear/libtommath/bn_mp_set_u64.c
        src/main/cpp/dropbear/libtommath/bn_mp_set_ul.c
        src/main/cpp/dropbear/libtommath/bn_mp_set_ull.c
        src/main/cpp/dropbear/libtommath/bn_mp_shrink.c
        src/main/cpp/dropbear/libtommath/bn_mp_signed_rsh.c
        src/main/cpp/dropbear/libtommath/bn_mp_sqr.c
        src/main/cpp/dropbear/libtommath/bn_mp_sqrmod.c
        src/main/cpp/dropbear/libtommath/bn_mp_sqrt.c
        src/main/cpp/dropbear/libtommath/bn_mp_sqrtmod_prime.c
        src/main/cpp/dropbear/libtommath/bn_mp_sub.c
        src/main/cpp/dropbear/libtommath/bn_mp_sub_d.c
        src/main/cpp/dropbear/libtommath/bn_mp_submod.c
        src/main/cpp/dropbear/libtommath/bn_mp_to_radix.c
        src/main/cpp/dropbear/libtommath/bn_mp_to_sbin.c
        src/main/cpp/dropbear/libtommath/bn_mp_to_ubin.c
        src/main/cpp/dropbear/libtommath/bn_mp_ubin_size.c
        src/main/cpp/dropbear/libtommath/bn_mp_unpack.c
        src/main/cpp/dropbear/libtommath/bn_mp_xor.c
        src/main/cpp/dropbear/libtommath/bn_mp_zero.c
        src/main/cpp/dropbear/libtommath/bn_prime_tab.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_add.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_balance_mul.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_exptmod.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_exptmod_fast.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_get_bit.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_invmod_fast.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_invmod_slow.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_karatsuba_mul.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_karatsuba_sqr.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_montgomery_reduce_fast.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_mul_digs.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_mul_digs_fast.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_mul_high_digs.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_mul_high_digs_fast.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_prime_is_divisible.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_rand_jenkins.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_rand_platform.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_reverse.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_sqr.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_sqr_fast.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_sub.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_toom_mul.c
        src/main/cpp/dropbear/libtommath/bn_s_mp_toom_sqr.c
        )

set(SRC_TOM_CRYPT
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/aes/aes.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/aes/aes_tab.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/anubis.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/blowfish.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/cast5.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/des.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/kasumi.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/khazad.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/kseed.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/multi2.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/noekeon.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/rc2.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/rc5.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/rc6.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/safer/safer.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/safer/saferp.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/skipjack.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/twofish/twofish.c
        src/main/cpp/dropbear/libtomcrypt/src/ciphers/xtea.c

        src/main/cpp/dropbear/libtomcrypt/src/encauth/ccm/ccm_add_aad.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ccm/ccm_add_nonce.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ccm/ccm_done.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ccm/ccm_init.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ccm/ccm_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ccm/ccm_process.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ccm/ccm_reset.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ccm/ccm_test.c

        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_add_aad.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_done.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_init.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_setiv.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_setiv_rfc7905.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/chachapoly/chacha20poly1305_test.c

        src/main/cpp/dropbear/libtomcrypt/src/encauth/eax/eax_addheader.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/eax/eax_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/eax/eax_decrypt_verify_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/eax/eax_done.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/eax/eax_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/eax/eax_encrypt_authenticate_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/eax/eax_init.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/eax/eax_test.c

        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_add_aad.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_add_iv.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_done.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_gf_mult.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_init.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_mult_h.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_process.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_reset.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/gcm/gcm_test.c

        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_decrypt_verify_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_done_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_done_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_encrypt_authenticate_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_init.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_ntz.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_shift_xor.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/ocb_test.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb/s_ocb_done.c

        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_add_aad.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_decrypt_last.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_decrypt_verify_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_done.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_encrypt_authenticate_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_encrypt_last.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_init.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_int_ntz.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_int_xor_blocks.c
        src/main/cpp/dropbear/libtomcrypt/src/encauth/ocb3/ocb3_test.c

        src/main/cpp/dropbear/libtomcrypt/src/hashes/blake2b.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/blake2s.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/chc/chc.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/helper/hash_file.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/helper/hash_filehandle.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/helper/hash_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/helper/hash_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/md2.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/md4.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/md5.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/rmd128.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/rmd160.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/rmd256.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/rmd320.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/sha1.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/sha2/sha256.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/sha2/sha384.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/sha2/sha512.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/sha2/sha512_224.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/sha2/sha512_256.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/sha3.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/sha3_test.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/tiger.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/whirl/whirl.c
        src/main/cpp/dropbear/libtomcrypt/src/hashes/whirl/whirltab.c

        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2bmac.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2bmac_file.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2bmac_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2bmac_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2bmac_test.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2smac.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2smac_file.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2smac_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2smac_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/blake2/blake2smac_test.c

        src/main/cpp/dropbear/libtomcrypt/src/mac/f9/f9_done.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/f9/f9_file.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/f9/f9_init.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/f9/f9_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/f9/f9_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/f9/f9_process.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/f9/f9_test.c

        src/main/cpp/dropbear/libtomcrypt/src/mac/hmac/hmac_done.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/hmac/hmac_file.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/hmac/hmac_init.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/hmac/hmac_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/hmac/hmac_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/hmac/hmac_process.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/hmac/hmac_test.c

        src/main/cpp/dropbear/libtomcrypt/src/mac/omac/omac_done.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/omac/omac_file.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/omac/omac_init.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/omac/omac_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/omac/omac_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/omac/omac_process.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/omac/omac_test.c

        src/main/cpp/dropbear/libtomcrypt/src/mac/pelican/pelican.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pelican/pelican_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pelican/pelican_test.c

        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_done.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_file.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_init.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_ntz.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_process.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_shift_xor.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/pmac/pmac_test.c

        src/main/cpp/dropbear/libtomcrypt/src/mac/poly1305/poly1305.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/poly1305/poly1305_file.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/poly1305/poly1305_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/poly1305/poly1305_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/poly1305/poly1305_test.c

        src/main/cpp/dropbear/libtomcrypt/src/mac/xcbc/xcbc_done.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/xcbc/xcbc_file.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/xcbc/xcbc_init.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/xcbc/xcbc_memory.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/xcbc/xcbc_memory_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/xcbc/xcbc_process.c
        src/main/cpp/dropbear/libtomcrypt/src/mac/xcbc/xcbc_test.c

        src/main/cpp/dropbear/libtomcrypt/src/math/fp/ltc_ecc_fp_mulmod.c
        src/main/cpp/dropbear/libtomcrypt/src/math/gmp_desc.c
        src/main/cpp/dropbear/libtomcrypt/src/math/ltm_desc.c
        src/main/cpp/dropbear/libtomcrypt/src/math/multi.c
        src/main/cpp/dropbear/libtomcrypt/src/math/radix_to_bin.c
        src/main/cpp/dropbear/libtomcrypt/src/math/rand_bn.c
        src/main/cpp/dropbear/libtomcrypt/src/math/rand_prime.c
        src/main/cpp/dropbear/libtomcrypt/src/math/tfm_desc.c

        src/main/cpp/dropbear/libtomcrypt/src/misc/adler32.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/base64/base64_decode.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/base64/base64_encode.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/burn_stack.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/compare_testvector.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crc32.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_argchk.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_cipher_descriptor.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_cipher_is_valid.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_constants.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_find_cipher.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_find_cipher_any.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_find_cipher_id.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_find_hash.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_find_hash_any.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_find_hash_id.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_find_hash_oid.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_find_prng.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_fsa.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_hash_descriptor.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_hash_is_valid.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_inits.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_ltc_mp_descriptor.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_prng_descriptor.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_prng_is_valid.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_prng_rng_descriptor.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_register_all_ciphers.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_register_all_hashes.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_register_all_prngs.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_register_cipher.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_register_hash.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_register_prng.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_sizes.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_unregister_cipher.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_unregister_hash.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/crypt/crypt_unregister_prng.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/error_to_string.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/hkdf/hkdf.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/hkdf/hkdf_test.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/mem_neq.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/pk_get_oid.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/pkcs5/pkcs_5_1.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/pkcs5/pkcs_5_2.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/pkcs5/pkcs_5_test.c
        src/main/cpp/dropbear/libtomcrypt/src/misc/zeromem.c

        src/main/cpp/dropbear/libtomcrypt/src/modes/cbc/cbc_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cbc/cbc_done.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cbc/cbc_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cbc/cbc_getiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cbc/cbc_setiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cbc/cbc_start.c

        src/main/cpp/dropbear/libtomcrypt/src/modes/cfb/cfb_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cfb/cfb_done.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cfb/cfb_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cfb/cfb_getiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cfb/cfb_setiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/cfb/cfb_start.c

        src/main/cpp/dropbear/libtomcrypt/src/modes/ctr/ctr_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ctr/ctr_done.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ctr/ctr_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ctr/ctr_getiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ctr/ctr_setiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ctr/ctr_start.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ctr/ctr_test.c

        src/main/cpp/dropbear/libtomcrypt/src/modes/ecb/ecb_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ecb/ecb_done.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ecb/ecb_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ecb/ecb_start.c

        src/main/cpp/dropbear/libtomcrypt/src/modes/f8/f8_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/f8/f8_done.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/f8/f8_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/f8/f8_getiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/f8/f8_setiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/f8/f8_start.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/f8/f8_test_mode.c

        src/main/cpp/dropbear/libtomcrypt/src/modes/lrw/lrw_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/lrw/lrw_done.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/lrw/lrw_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/lrw/lrw_getiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/lrw/lrw_process.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/lrw/lrw_setiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/lrw/lrw_start.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/lrw/lrw_test.c

        src/main/cpp/dropbear/libtomcrypt/src/modes/ofb/ofb_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ofb/ofb_done.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ofb/ofb_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ofb/ofb_getiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ofb/ofb_setiv.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/ofb/ofb_start.c

        src/main/cpp/dropbear/libtomcrypt/src/modes/xts/xts_decrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/xts/xts_done.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/xts/xts_encrypt.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/xts/xts_init.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/xts/xts_mult_x.c
        src/main/cpp/dropbear/libtomcrypt/src/modes/xts/xts_test.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/bit/der_decode_bit_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/bit/der_decode_raw_bit_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/bit/der_encode_bit_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/bit/der_encode_raw_bit_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/bit/der_length_bit_string.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/boolean/der_decode_boolean.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/boolean/der_encode_boolean.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/boolean/der_length_boolean.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/choice/der_decode_choice.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/generalizedtime/der_decode_generalizedtime.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/generalizedtime/der_encode_generalizedtime.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/generalizedtime/der_length_generalizedtime.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/ia5/der_decode_ia5_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/ia5/der_encode_ia5_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/ia5/der_length_ia5_string.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/integer/der_decode_integer.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/integer/der_encode_integer.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/integer/der_length_integer.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/object_identifier/der_decode_object_identifier.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/object_identifier/der_encode_object_identifier.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/object_identifier/der_length_object_identifier.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/octet/der_decode_octet_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/octet/der_encode_octet_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/octet/der_length_octet_string.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/printable_string/der_decode_printable_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/printable_string/der_encode_printable_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/printable_string/der_length_printable_string.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_decode_sequence_ex.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_decode_sequence_flexi.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_decode_sequence_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_decode_subject_public_key_info.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_encode_sequence_ex.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_encode_sequence_multi.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_encode_subject_public_key_info.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_length_sequence.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_sequence_free.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/sequence/der_sequence_shrink.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/set/der_encode_set.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/set/der_encode_setof.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/short_integer/der_decode_short_integer.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/short_integer/der_encode_short_integer.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/short_integer/der_length_short_integer.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/teletex_string/der_decode_teletex_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/teletex_string/der_length_teletex_string.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/utctime/der_decode_utctime.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/utctime/der_encode_utctime.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/utctime/der_length_utctime.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/utf8/der_decode_utf8_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/utf8/der_encode_utf8_string.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/asn1/der/utf8/der_length_utf8_string.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_decrypt_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_encrypt_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_export.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_free.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_generate_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_generate_pqg.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_import.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_make_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_set.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_shared_secret.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_sign_hash.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_verify_hash.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/dsa/dsa_verify_key.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_ansi_x963_export.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_ansi_x963_import.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_decrypt_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_encrypt_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_export.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_free.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_get_size.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_import.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_make_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_shared_secret.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_sign_hash.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_sizes.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_test.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ecc_verify_hash.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ltc_ecc_is_valid_idx.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ltc_ecc_map.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ltc_ecc_mul2add.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ltc_ecc_mulmod.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ltc_ecc_mulmod_timing.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ltc_ecc_points.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ltc_ecc_projective_add_point.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/ecc/ltc_ecc_projective_dbl_point.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/katja/katja_decrypt_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/katja/katja_encrypt_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/katja/katja_export.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/katja/katja_exptmod.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/katja/katja_free.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/katja/katja_import.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/katja/katja_make_key.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_i2osp.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_mgf1.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_oaep_decode.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_oaep_encode.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_os2ip.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_pss_decode.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_pss_encode.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_v1_5_decode.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/pkcs1/pkcs_1_v1_5_encode.c

        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_decrypt_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_encrypt_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_export.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_exptmod.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_free.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_get_size.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_import.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_import_pkcs8.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_import_x509.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_make_key.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_set.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_sign_hash.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_sign_saltlen_get.c
        src/main/cpp/dropbear/libtomcrypt/src/pk/rsa/rsa_verify_hash.c

        src/main/cpp/dropbear/libtomcrypt/src/prngs/chacha20.c
        src/main/cpp/dropbear/libtomcrypt/src/prngs/fortuna.c
        src/main/cpp/dropbear/libtomcrypt/src/prngs/rc4.c
        src/main/cpp/dropbear/libtomcrypt/src/prngs/rng_get_bytes.c
        src/main/cpp/dropbear/libtomcrypt/src/prngs/rng_make_prng.c
        src/main/cpp/dropbear/libtomcrypt/src/prngs/sober128.c
        # src/main/cpp/dropbear/libtomcrypt/src/prngs/sober128tab.c
        src/main/cpp/dropbear/libtomcrypt/src/prngs/sprng.c
        src/main/cpp/dropbear/libtomcrypt/src/prngs/yarrow.c

        src/main/cpp/dropbear/libtomcrypt/src/stream/chacha/chacha_crypt.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/chacha/chacha_done.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/chacha/chacha_ivctr32.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/chacha/chacha_ivctr64.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/chacha/chacha_keystream.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/chacha/chacha_setup.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/chacha/chacha_test.c

        src/main/cpp/dropbear/libtomcrypt/src/stream/rc4/rc4_stream.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/rc4/rc4_test.c

        src/main/cpp/dropbear/libtomcrypt/src/stream/sober128/sober128_stream.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/sober128/sober128_test.c
        src/main/cpp/dropbear/libtomcrypt/src/stream/sober128/sober128tab.c
        )

set(SRC_DROPBEAR
        src/main/cpp/dropbear/atomicio.c
        src/main/cpp/dropbear/bignum.c
        src/main/cpp/dropbear/buffer.c
        src/main/cpp/dropbear/chachapoly.c
        src/main/cpp/dropbear/circbuffer.c
        src/main/cpp/dropbear/common-algo.c
        src/main/cpp/dropbear/common-channel.c
        src/main/cpp/dropbear/common-chansession.c
        src/main/cpp/dropbear/common-kex.c
        src/main/cpp/dropbear/common-runopts.c
        src/main/cpp/dropbear/common-session.c
        src/main/cpp/dropbear/compat.c
        src/main/cpp/dropbear/crypto_desc.c
        src/main/cpp/dropbear/curve25519.c
        src/main/cpp/dropbear/dbhelpers.c
        src/main/cpp/dropbear/dbmalloc.c
        src/main/cpp/dropbear/dbrandom.c
        src/main/cpp/dropbear/dbutil.c
        src/main/cpp/dropbear/dh_groups.c
        src/main/cpp/dropbear/dss.c
        src/main/cpp/dropbear/ecc.c
        src/main/cpp/dropbear/ecdsa.c
        src/main/cpp/dropbear/ed25519.c
        src/main/cpp/dropbear/gcm.c
        src/main/cpp/dropbear/gendss.c
        src/main/cpp/dropbear/gened25519.c
        src/main/cpp/dropbear/genrsa.c
        src/main/cpp/dropbear/gensignkey.c
        src/main/cpp/dropbear/keyimport.c
        src/main/cpp/dropbear/list.c
        src/main/cpp/dropbear/listener.c
        src/main/cpp/dropbear/loginrec.c
        src/main/cpp/dropbear/ltc_prng.c
        src/main/cpp/dropbear/netio.c
        src/main/cpp/dropbear/packet.c
        src/main/cpp/dropbear/process-packet.c
        src/main/cpp/dropbear/queue.c
        src/main/cpp/dropbear/rsa.c
        src/main/cpp/dropbear/signkey.c
        src/main/cpp/dropbear/signkey_ossh.c
        src/main/cpp/dropbear/sk-ecdsa.c
        src/main/cpp/dropbear/sk-ed25519.c
        src/main/cpp/dropbear/sshpty.c
        src/main/cpp/dropbear/svr-auth.c
        src/main/cpp/dropbear/svr-authpasswd.c
        src/main/cpp/dropbear/svr-authpubkey.c
        src/main/cpp/dropbear/svr-authpubkeyoptions.c
        src/main/cpp/dropbear/svr-chansession.c
        src/main/cpp/dropbear/svr-kex.c
        src/main/cpp/dropbear/svr-main.c
        src/main/cpp/dropbear/svr-runopts.c
        src/main/cpp/dropbear/svr-service.c
        src/main/cpp/dropbear/svr-session.c
        src/main/cpp/dropbear/svr-tcpfwd.c
        src/main/cpp/dropbear/tcp-accept.c
        src/main/cpp/dropbear/termcodes.c
        )

set(SRC_DROPBEAR_SCP
        src/main/cpp/dropbear/scp.c
        src/main/cpp/dropbear/scpmisc.c
        src/main/cpp/dropbear/atomicio.c
        )

set(SRC_SFTP_SERVER
        src/main/cpp/openssh/openbsd-compat/bsd-misc.c
        src/main/cpp/openssh/openbsd-compat/explicit_bzero.c
        src/main/cpp/openssh/openbsd-compat/fmt_scaled.c
        src/main/cpp/openssh/openbsd-compat/freezero.c
        src/main/cpp/openssh/openbsd-compat/getopt_long.c
        src/main/cpp/openssh/openbsd-compat/pwcache.c
        src/main/cpp/openssh/openbsd-compat/reallocarray.c
        src/main/cpp/openssh/openbsd-compat/recallocarray.c
        src/main/cpp/openssh/openbsd-compat/strmode.c
        src/main/cpp/openssh/openbsd-compat/vis.c
        src/main/cpp/openssh/atomicio.c
        src/main/cpp/openssh/entropy.c
        src/main/cpp/openssh/fatal.c
        src/main/cpp/openssh/log.c
        src/main/cpp/openssh/platform-pledge.c
        src/main/cpp/openssh/platform-tracing.c
        src/main/cpp/openssh/match.c
        src/main/cpp/openssh/misc.c
        src/main/cpp/openssh/sftp-common.c
        src/main/cpp/openssh/sftp-realpath.c
        src/main/cpp/openssh/sftp-server.c
        src/main/cpp/openssh/sftp-server-main.c
        src/main/cpp/openssh/sshbuf.c
        src/main/cpp/openssh/sshbuf-misc.c
        src/main/cpp/openssh/sshbuf-getput-basic.c
        src/main/cpp/openssh/ssherr.c
        src/main/cpp/openssh/xmalloc.c
        )

set(SRC_RSYNC
        src/main/cpp/rsync/lib/compat.c
        src/main/cpp/rsync/lib/getpass.c
        src/main/cpp/rsync/lib/md5.c
        src/main/cpp/rsync/lib/mdfour.c
        src/main/cpp/rsync/lib/permstring.c
        src/main/cpp/rsync/lib/pool_alloc.c
        src/main/cpp/rsync/lib/snprintf.c
        src/main/cpp/rsync/lib/sysacls.c
        src/main/cpp/rsync/lib/sysxattrs.c
        src/main/cpp/rsync/lib/wildmatch.c

        src/main/cpp/rsync/popt/findme.c
        src/main/cpp/rsync/popt/popt.c
        src/main/cpp/rsync/popt/poptconfig.c
        src/main/cpp/rsync/popt/popthelp.c
        src/main/cpp/rsync/popt/poptparse.c

        src/main/cpp/rsync/zlib/adler32.c
        src/main/cpp/rsync/zlib/compress.c
        src/main/cpp/rsync/zlib/crc32.c
        src/main/cpp/rsync/zlib/deflate.c
        src/main/cpp/rsync/zlib/inffast.c
        src/main/cpp/rsync/zlib/inflate.c
        src/main/cpp/rsync/zlib/inftrees.c
        src/main/cpp/rsync/zlib/trees.c
        src/main/cpp/rsync/zlib/zutil.c

        src/main/cpp/rsync/access.c
        src/main/cpp/rsync/acls.c
        src/main/cpp/rsync/authenticate.c
        src/main/cpp/rsync/backup.c
        src/main/cpp/rsync/batch.c
        src/main/cpp/rsync/checksum.c
        src/main/cpp/rsync/chmod.c
        src/main/cpp/rsync/cleanup.c
        src/main/cpp/rsync/clientname.c
        src/main/cpp/rsync/clientserver.c
        src/main/cpp/rsync/compat.c
        src/main/cpp/rsync/connection.c
        src/main/cpp/rsync/delete.c
        src/main/cpp/rsync/exclude.c
        src/main/cpp/rsync/fileio.c
        src/main/cpp/rsync/flist.c
        src/main/cpp/rsync/generator.c
        src/main/cpp/rsync/hashtable.c
        src/main/cpp/rsync/hlink.c
        src/main/cpp/rsync/io.c
        src/main/cpp/rsync/loadparm.c
        src/main/cpp/rsync/log.c
        src/main/cpp/rsync/main.c
        src/main/cpp/rsync/match.c
        src/main/cpp/rsync/options.c
        src/main/cpp/rsync/params.c
        src/main/cpp/rsync/pipe.c
        src/main/cpp/rsync/progress.c
        src/main/cpp/rsync/receiver.c
        src/main/cpp/rsync/rsync.c
        src/main/cpp/rsync/sender.c
        src/main/cpp/rsync/socket.c
        src/main/cpp/rsync/syscall.c
        src/main/cpp/rsync/token.c
        src/main/cpp/rsync/uidlist.c
        src/main/cpp/rsync/usage.c
        src/main/cpp/rsync/util1.c
        src/main/cpp/rsync/util2.c
        src/main/cpp/rsync/xattrs.c
        )
# #############################################################################

# compile tommath as an object library; to be statically linked later.
add_library(tommath OBJECT ${SRC_TOM_MATH})
target_include_directories(tommath PRIVATE
        src/main/cpp/dropbear
        src/main/cpp/dropbear/libtomcrypt/src/headers
        src/main/cpp/dropbear/libtommath
        )

# compile tomcrypt as an object library; to be statically linked later.
add_library(tomcrypt OBJECT ${SRC_TOM_CRYPT})
# LTC_SOURCE is ONLY valid/used for tomcrypt.
target_compile_definitions(tomcrypt PRIVATE LTC_SOURCE)
target_include_directories(tomcrypt PRIVATE
        src/main/cpp/dropbear
        src/main/cpp/dropbear/libtomcrypt/src/headers
        src/main/cpp/dropbear/libtommath
        )

# compile dropbear as an object library; to be statically linked later.
add_library(dropbear OBJECT ${SRC_DROPBEAR})
target_include_directories(dropbear PRIVATE
        src/main/cpp/dropbear
        src/main/cpp/dropbear/libtomcrypt/src/headers
        src/main/cpp/dropbear/libtommath
        )

# finally, build the jni code with embedded dropbear by
# linking it with the above object libraries + zlib from the system.
add_library(jni-dropbear SHARED src/main/cpp/jni-dropbear.c)
target_link_options(jni-dropbear PRIVATE -lz)
target_link_libraries(jni-dropbear tommath tomcrypt dropbear)

# build scp executable
add_executable(libscp.so ${SRC_DROPBEAR_SCP})
target_include_directories(libscp.so PRIVATE
        src/main/cpp/dropbear
        src/main/cpp/dropbear/libtomcrypt/src/headers
        src/main/cpp/dropbear/libtommath
        )

# #############################################################################
# build sftp executable
add_executable(libsftp-server.so ${SRC_SFTP_SERVER})
target_include_directories(libsftp-server.so PRIVATE
        src/main/cpp/openssh
        src/main/cpp/openssh/openbsd-compat
        )

# #############################################################################
# build rsync executable
# use internal zlib; see app/src/main/cpp/rsync/zlib/README.rsync
add_executable(librsync.so ${SRC_RSYNC})
# needed for popt
target_compile_definitions(librsync.so PRIVATE HAVE_CONFIG_H)
target_include_directories(librsync.so PRIVATE
        src/main/cpp/rsync
        src/main/cpp/rsync/popt
        src/main/cpp/rsync/zlib
        )

# #############################################################################

# build buffersu executable
add_executable(libbuffersu.so src/main/cpp/buffersu/buffersu.c)
