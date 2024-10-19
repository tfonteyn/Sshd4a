cmake_minimum_required(VERSION 3.30.5)

project(Sshd4a)

# #############################################################################
# define simple variables with the sources grouped purely for easier editing
#
# - tommath/tomcrypt has (nearly) all files listed whether we use them or not.
# - dropbear, openssh, rsync only(?) lists the files we actually need.
# TODO: prune unneeded files

set(BASE_SRC_TOM_MATH    src/main/cpp/dropbear/libtommath)
set(BASE_SRC_TOM_CRYPT   src/main/cpp/dropbear/libtomcrypt/src)
set(BASE_SRC_DROPBEAR    src/main/cpp/dropbear/src)
set(BASE_SRC_RSYNC       src/main/cpp/rsync)
set(BASE_SRC_SFTP_SERVER src/main/cpp/openssh)

set(DROPBEAR_INCLUDES
        src/main/cpp/dropbear
        ${BASE_SRC_DROPBEAR}
        ${BASE_SRC_TOM_CRYPT}/headers
        ${BASE_SRC_TOM_MATH}
        )

set(SRC_TOM_MATH
        ${BASE_SRC_TOM_MATH}/bn_cutoffs.c
        ${BASE_SRC_TOM_MATH}/bn_mp_2expt.c
        ${BASE_SRC_TOM_MATH}/bn_mp_abs.c
        ${BASE_SRC_TOM_MATH}/bn_mp_add.c
        ${BASE_SRC_TOM_MATH}/bn_mp_add_d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_addmod.c
        ${BASE_SRC_TOM_MATH}/bn_mp_and.c
        ${BASE_SRC_TOM_MATH}/bn_mp_clamp.c
        ${BASE_SRC_TOM_MATH}/bn_mp_clear.c
        ${BASE_SRC_TOM_MATH}/bn_mp_clear_multi.c
        ${BASE_SRC_TOM_MATH}/bn_mp_cmp.c
        ${BASE_SRC_TOM_MATH}/bn_mp_cmp_d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_cmp_mag.c
        ${BASE_SRC_TOM_MATH}/bn_mp_cnt_lsb.c
        ${BASE_SRC_TOM_MATH}/bn_mp_complement.c
        ${BASE_SRC_TOM_MATH}/bn_mp_copy.c
        ${BASE_SRC_TOM_MATH}/bn_mp_count_bits.c
        ${BASE_SRC_TOM_MATH}/bn_mp_decr.c
        ${BASE_SRC_TOM_MATH}/bn_mp_div.c
        ${BASE_SRC_TOM_MATH}/bn_mp_div_2.c
        ${BASE_SRC_TOM_MATH}/bn_mp_div_2d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_div_3.c
        ${BASE_SRC_TOM_MATH}/bn_mp_div_d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_dr_is_modulus.c
        ${BASE_SRC_TOM_MATH}/bn_mp_dr_reduce.c
        ${BASE_SRC_TOM_MATH}/bn_mp_dr_setup.c
        ${BASE_SRC_TOM_MATH}/bn_mp_error_to_string.c
        ${BASE_SRC_TOM_MATH}/bn_mp_exch.c
        ${BASE_SRC_TOM_MATH}/bn_mp_expt_u32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_exptmod.c
        ${BASE_SRC_TOM_MATH}/bn_mp_exteuclid.c
        ${BASE_SRC_TOM_MATH}/bn_mp_fread.c
        ${BASE_SRC_TOM_MATH}/bn_mp_from_sbin.c
        ${BASE_SRC_TOM_MATH}/bn_mp_from_ubin.c
        ${BASE_SRC_TOM_MATH}/bn_mp_fwrite.c
        ${BASE_SRC_TOM_MATH}/bn_mp_gcd.c
        ${BASE_SRC_TOM_MATH}/bn_mp_get_i32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_get_i64.c
        ${BASE_SRC_TOM_MATH}/bn_mp_get_l.c
        ${BASE_SRC_TOM_MATH}/bn_mp_get_ll.c
        ${BASE_SRC_TOM_MATH}/bn_mp_get_mag_u32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_get_mag_u64.c
        ${BASE_SRC_TOM_MATH}/bn_mp_get_mag_ul.c
        ${BASE_SRC_TOM_MATH}/bn_mp_get_mag_ull.c
        ${BASE_SRC_TOM_MATH}/bn_mp_grow.c
        ${BASE_SRC_TOM_MATH}/bn_mp_incr.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_copy.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_i32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_i64.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_l.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_ll.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_multi.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_set.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_size.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_u32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_u64.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_ul.c
        ${BASE_SRC_TOM_MATH}/bn_mp_init_ull.c
        ${BASE_SRC_TOM_MATH}/bn_mp_invmod.c
        ${BASE_SRC_TOM_MATH}/bn_mp_is_square.c
        ${BASE_SRC_TOM_MATH}/bn_mp_iseven.c
        ${BASE_SRC_TOM_MATH}/bn_mp_isodd.c
        ${BASE_SRC_TOM_MATH}/bn_mp_kronecker.c
        ${BASE_SRC_TOM_MATH}/bn_mp_lcm.c
        ${BASE_SRC_TOM_MATH}/bn_mp_log_u32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_lshd.c
        ${BASE_SRC_TOM_MATH}/bn_mp_mod.c
        ${BASE_SRC_TOM_MATH}/bn_mp_mod_2d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_mod_d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_montgomery_calc_normalization.c
        ${BASE_SRC_TOM_MATH}/bn_mp_montgomery_reduce.c
        ${BASE_SRC_TOM_MATH}/bn_mp_montgomery_setup.c
        ${BASE_SRC_TOM_MATH}/bn_mp_mul.c
        ${BASE_SRC_TOM_MATH}/bn_mp_mul_2.c
        ${BASE_SRC_TOM_MATH}/bn_mp_mul_2d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_mul_d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_mulmod.c
        ${BASE_SRC_TOM_MATH}/bn_mp_neg.c
        ${BASE_SRC_TOM_MATH}/bn_mp_or.c
        ${BASE_SRC_TOM_MATH}/bn_mp_pack.c
        ${BASE_SRC_TOM_MATH}/bn_mp_pack_count.c
        ${BASE_SRC_TOM_MATH}/bn_mp_prime_fermat.c
        ${BASE_SRC_TOM_MATH}/bn_mp_prime_frobenius_underwood.c
        ${BASE_SRC_TOM_MATH}/bn_mp_prime_is_prime.c
        ${BASE_SRC_TOM_MATH}/bn_mp_prime_miller_rabin.c
        ${BASE_SRC_TOM_MATH}/bn_mp_prime_next_prime.c
        ${BASE_SRC_TOM_MATH}/bn_mp_prime_rabin_miller_trials.c
        ${BASE_SRC_TOM_MATH}/bn_mp_prime_rand.c
        ${BASE_SRC_TOM_MATH}/bn_mp_prime_strong_lucas_selfridge.c
        ${BASE_SRC_TOM_MATH}/bn_mp_radix_size.c
        ${BASE_SRC_TOM_MATH}/bn_mp_radix_smap.c
        ${BASE_SRC_TOM_MATH}/bn_mp_rand.c
        ${BASE_SRC_TOM_MATH}/bn_mp_read_radix.c
        ${BASE_SRC_TOM_MATH}/bn_mp_reduce.c
        ${BASE_SRC_TOM_MATH}/bn_mp_reduce_2k.c
        ${BASE_SRC_TOM_MATH}/bn_mp_reduce_2k_l.c
        ${BASE_SRC_TOM_MATH}/bn_mp_reduce_2k_setup.c
        ${BASE_SRC_TOM_MATH}/bn_mp_reduce_2k_setup_l.c
        ${BASE_SRC_TOM_MATH}/bn_mp_reduce_is_2k.c
        ${BASE_SRC_TOM_MATH}/bn_mp_reduce_is_2k_l.c
        ${BASE_SRC_TOM_MATH}/bn_mp_reduce_setup.c
        ${BASE_SRC_TOM_MATH}/bn_mp_root_u32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_rshd.c
        ${BASE_SRC_TOM_MATH}/bn_mp_sbin_size.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set_i32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set_i64.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set_l.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set_ll.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set_u32.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set_u64.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set_ul.c
        ${BASE_SRC_TOM_MATH}/bn_mp_set_ull.c
        ${BASE_SRC_TOM_MATH}/bn_mp_shrink.c
        ${BASE_SRC_TOM_MATH}/bn_mp_signed_rsh.c
        ${BASE_SRC_TOM_MATH}/bn_mp_sqr.c
        ${BASE_SRC_TOM_MATH}/bn_mp_sqrmod.c
        ${BASE_SRC_TOM_MATH}/bn_mp_sqrt.c
        ${BASE_SRC_TOM_MATH}/bn_mp_sqrtmod_prime.c
        ${BASE_SRC_TOM_MATH}/bn_mp_sub.c
        ${BASE_SRC_TOM_MATH}/bn_mp_sub_d.c
        ${BASE_SRC_TOM_MATH}/bn_mp_submod.c
        ${BASE_SRC_TOM_MATH}/bn_mp_to_radix.c
        ${BASE_SRC_TOM_MATH}/bn_mp_to_sbin.c
        ${BASE_SRC_TOM_MATH}/bn_mp_to_ubin.c
        ${BASE_SRC_TOM_MATH}/bn_mp_ubin_size.c
        ${BASE_SRC_TOM_MATH}/bn_mp_unpack.c
        ${BASE_SRC_TOM_MATH}/bn_mp_xor.c
        ${BASE_SRC_TOM_MATH}/bn_mp_zero.c
        ${BASE_SRC_TOM_MATH}/bn_prime_tab.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_add.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_balance_mul.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_exptmod.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_exptmod_fast.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_get_bit.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_invmod_fast.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_invmod_slow.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_karatsuba_mul.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_karatsuba_sqr.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_montgomery_reduce_fast.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_mul_digs.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_mul_digs_fast.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_mul_high_digs.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_mul_high_digs_fast.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_prime_is_divisible.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_rand_jenkins.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_rand_platform.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_reverse.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_sqr.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_sqr_fast.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_sub.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_toom_mul.c
        ${BASE_SRC_TOM_MATH}/bn_s_mp_toom_sqr.c
        )

set(SRC_TOM_CRYPT
        ${BASE_SRC_TOM_CRYPT}/ciphers/aes/aes.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/aes/aes_tab.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/anubis.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/blowfish.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/cast5.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/des.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/kasumi.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/khazad.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/kseed.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/multi2.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/noekeon.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/rc2.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/rc5.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/rc6.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/safer/safer.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/safer/saferp.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/skipjack.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/twofish/twofish.c
        ${BASE_SRC_TOM_CRYPT}/ciphers/xtea.c

        ${BASE_SRC_TOM_CRYPT}/encauth/ccm/ccm_add_aad.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ccm/ccm_add_nonce.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ccm/ccm_done.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ccm/ccm_init.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ccm/ccm_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ccm/ccm_process.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ccm/ccm_reset.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ccm/ccm_test.c

        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_add_aad.c
        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_done.c
        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_init.c
        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_setiv.c
        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_setiv_rfc7905.c
        ${BASE_SRC_TOM_CRYPT}/encauth/chachapoly/chacha20poly1305_test.c

        ${BASE_SRC_TOM_CRYPT}/encauth/eax/eax_addheader.c
        ${BASE_SRC_TOM_CRYPT}/encauth/eax/eax_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/eax/eax_decrypt_verify_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/eax/eax_done.c
        ${BASE_SRC_TOM_CRYPT}/encauth/eax/eax_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/eax/eax_encrypt_authenticate_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/eax/eax_init.c
        ${BASE_SRC_TOM_CRYPT}/encauth/eax/eax_test.c

        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_add_aad.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_add_iv.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_done.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_gf_mult.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_init.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_mult_h.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_process.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_reset.c
        ${BASE_SRC_TOM_CRYPT}/encauth/gcm/gcm_test.c

        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_decrypt_verify_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_done_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_done_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_encrypt_authenticate_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_init.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_ntz.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_shift_xor.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/ocb_test.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb/s_ocb_done.c

        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_add_aad.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_decrypt_last.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_decrypt_verify_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_done.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_encrypt_authenticate_memory.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_encrypt_last.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_init.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_int_ntz.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_int_xor_blocks.c
        ${BASE_SRC_TOM_CRYPT}/encauth/ocb3/ocb3_test.c

        ${BASE_SRC_TOM_CRYPT}/hashes/blake2b.c
        ${BASE_SRC_TOM_CRYPT}/hashes/blake2s.c
        ${BASE_SRC_TOM_CRYPT}/hashes/chc/chc.c
        ${BASE_SRC_TOM_CRYPT}/hashes/helper/hash_file.c
        ${BASE_SRC_TOM_CRYPT}/hashes/helper/hash_filehandle.c
        ${BASE_SRC_TOM_CRYPT}/hashes/helper/hash_memory.c
        ${BASE_SRC_TOM_CRYPT}/hashes/helper/hash_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/hashes/md2.c
        ${BASE_SRC_TOM_CRYPT}/hashes/md4.c
        ${BASE_SRC_TOM_CRYPT}/hashes/md5.c
        ${BASE_SRC_TOM_CRYPT}/hashes/rmd128.c
        ${BASE_SRC_TOM_CRYPT}/hashes/rmd160.c
        ${BASE_SRC_TOM_CRYPT}/hashes/rmd256.c
        ${BASE_SRC_TOM_CRYPT}/hashes/rmd320.c
        ${BASE_SRC_TOM_CRYPT}/hashes/sha1.c
        ${BASE_SRC_TOM_CRYPT}/hashes/sha2/sha256.c
        ${BASE_SRC_TOM_CRYPT}/hashes/sha2/sha384.c
        ${BASE_SRC_TOM_CRYPT}/hashes/sha2/sha512.c
        ${BASE_SRC_TOM_CRYPT}/hashes/sha2/sha512_224.c
        ${BASE_SRC_TOM_CRYPT}/hashes/sha2/sha512_256.c
        ${BASE_SRC_TOM_CRYPT}/hashes/sha3.c
        ${BASE_SRC_TOM_CRYPT}/hashes/sha3_test.c
        ${BASE_SRC_TOM_CRYPT}/hashes/tiger.c
        ${BASE_SRC_TOM_CRYPT}/hashes/whirl/whirl.c
        ${BASE_SRC_TOM_CRYPT}/hashes/whirl/whirltab.c

        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2bmac.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2bmac_file.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2bmac_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2bmac_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2bmac_test.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2smac.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2smac_file.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2smac_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2smac_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/mac/blake2/blake2smac_test.c

        ${BASE_SRC_TOM_CRYPT}/mac/f9/f9_done.c
        ${BASE_SRC_TOM_CRYPT}/mac/f9/f9_file.c
        ${BASE_SRC_TOM_CRYPT}/mac/f9/f9_init.c
        ${BASE_SRC_TOM_CRYPT}/mac/f9/f9_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/f9/f9_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/mac/f9/f9_process.c
        ${BASE_SRC_TOM_CRYPT}/mac/f9/f9_test.c

        ${BASE_SRC_TOM_CRYPT}/mac/hmac/hmac_done.c
        ${BASE_SRC_TOM_CRYPT}/mac/hmac/hmac_file.c
        ${BASE_SRC_TOM_CRYPT}/mac/hmac/hmac_init.c
        ${BASE_SRC_TOM_CRYPT}/mac/hmac/hmac_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/hmac/hmac_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/mac/hmac/hmac_process.c
        ${BASE_SRC_TOM_CRYPT}/mac/hmac/hmac_test.c

        ${BASE_SRC_TOM_CRYPT}/mac/omac/omac_done.c
        ${BASE_SRC_TOM_CRYPT}/mac/omac/omac_file.c
        ${BASE_SRC_TOM_CRYPT}/mac/omac/omac_init.c
        ${BASE_SRC_TOM_CRYPT}/mac/omac/omac_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/omac/omac_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/mac/omac/omac_process.c
        ${BASE_SRC_TOM_CRYPT}/mac/omac/omac_test.c

        ${BASE_SRC_TOM_CRYPT}/mac/pelican/pelican.c
        ${BASE_SRC_TOM_CRYPT}/mac/pelican/pelican_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/pelican/pelican_test.c

        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_done.c
        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_file.c
        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_init.c
        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_ntz.c
        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_process.c
        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_shift_xor.c
        ${BASE_SRC_TOM_CRYPT}/mac/pmac/pmac_test.c

        ${BASE_SRC_TOM_CRYPT}/mac/poly1305/poly1305.c
        ${BASE_SRC_TOM_CRYPT}/mac/poly1305/poly1305_file.c
        ${BASE_SRC_TOM_CRYPT}/mac/poly1305/poly1305_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/poly1305/poly1305_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/mac/poly1305/poly1305_test.c

        ${BASE_SRC_TOM_CRYPT}/mac/xcbc/xcbc_done.c
        ${BASE_SRC_TOM_CRYPT}/mac/xcbc/xcbc_file.c
        ${BASE_SRC_TOM_CRYPT}/mac/xcbc/xcbc_init.c
        ${BASE_SRC_TOM_CRYPT}/mac/xcbc/xcbc_memory.c
        ${BASE_SRC_TOM_CRYPT}/mac/xcbc/xcbc_memory_multi.c
        ${BASE_SRC_TOM_CRYPT}/mac/xcbc/xcbc_process.c
        ${BASE_SRC_TOM_CRYPT}/mac/xcbc/xcbc_test.c

        ${BASE_SRC_TOM_CRYPT}/math/fp/ltc_ecc_fp_mulmod.c
        ${BASE_SRC_TOM_CRYPT}/math/gmp_desc.c
        ${BASE_SRC_TOM_CRYPT}/math/ltm_desc.c
        ${BASE_SRC_TOM_CRYPT}/math/multi.c
        ${BASE_SRC_TOM_CRYPT}/math/radix_to_bin.c
        ${BASE_SRC_TOM_CRYPT}/math/rand_bn.c
        ${BASE_SRC_TOM_CRYPT}/math/rand_prime.c
        ${BASE_SRC_TOM_CRYPT}/math/tfm_desc.c

        ${BASE_SRC_TOM_CRYPT}/misc/adler32.c
        ${BASE_SRC_TOM_CRYPT}/misc/base64/base64_decode.c
        ${BASE_SRC_TOM_CRYPT}/misc/base64/base64_encode.c
        ${BASE_SRC_TOM_CRYPT}/misc/burn_stack.c
        ${BASE_SRC_TOM_CRYPT}/misc/compare_testvector.c
        ${BASE_SRC_TOM_CRYPT}/misc/crc32.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_argchk.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_cipher_descriptor.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_cipher_is_valid.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_constants.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_find_cipher.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_find_cipher_any.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_find_cipher_id.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_find_hash.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_find_hash_any.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_find_hash_id.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_find_hash_oid.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_find_prng.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_fsa.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_hash_descriptor.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_hash_is_valid.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_inits.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_ltc_mp_descriptor.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_prng_descriptor.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_prng_is_valid.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_prng_rng_descriptor.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_register_all_ciphers.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_register_all_hashes.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_register_all_prngs.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_register_cipher.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_register_hash.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_register_prng.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_sizes.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_unregister_cipher.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_unregister_hash.c
        ${BASE_SRC_TOM_CRYPT}/misc/crypt/crypt_unregister_prng.c
        ${BASE_SRC_TOM_CRYPT}/misc/error_to_string.c
        ${BASE_SRC_TOM_CRYPT}/misc/hkdf/hkdf.c
        ${BASE_SRC_TOM_CRYPT}/misc/hkdf/hkdf_test.c
        ${BASE_SRC_TOM_CRYPT}/misc/mem_neq.c
        ${BASE_SRC_TOM_CRYPT}/misc/pk_get_oid.c
        ${BASE_SRC_TOM_CRYPT}/misc/pkcs5/pkcs_5_1.c
        ${BASE_SRC_TOM_CRYPT}/misc/pkcs5/pkcs_5_2.c
        ${BASE_SRC_TOM_CRYPT}/misc/pkcs5/pkcs_5_test.c
        ${BASE_SRC_TOM_CRYPT}/misc/zeromem.c

        ${BASE_SRC_TOM_CRYPT}/modes/cbc/cbc_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/cbc/cbc_done.c
        ${BASE_SRC_TOM_CRYPT}/modes/cbc/cbc_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/cbc/cbc_getiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/cbc/cbc_setiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/cbc/cbc_start.c

        ${BASE_SRC_TOM_CRYPT}/modes/cfb/cfb_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/cfb/cfb_done.c
        ${BASE_SRC_TOM_CRYPT}/modes/cfb/cfb_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/cfb/cfb_getiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/cfb/cfb_setiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/cfb/cfb_start.c

        ${BASE_SRC_TOM_CRYPT}/modes/ctr/ctr_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/ctr/ctr_done.c
        ${BASE_SRC_TOM_CRYPT}/modes/ctr/ctr_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/ctr/ctr_getiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/ctr/ctr_setiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/ctr/ctr_start.c
        ${BASE_SRC_TOM_CRYPT}/modes/ctr/ctr_test.c

        ${BASE_SRC_TOM_CRYPT}/modes/ecb/ecb_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/ecb/ecb_done.c
        ${BASE_SRC_TOM_CRYPT}/modes/ecb/ecb_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/ecb/ecb_start.c

        ${BASE_SRC_TOM_CRYPT}/modes/f8/f8_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/f8/f8_done.c
        ${BASE_SRC_TOM_CRYPT}/modes/f8/f8_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/f8/f8_getiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/f8/f8_setiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/f8/f8_start.c
        ${BASE_SRC_TOM_CRYPT}/modes/f8/f8_test_mode.c

        ${BASE_SRC_TOM_CRYPT}/modes/lrw/lrw_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/lrw/lrw_done.c
        ${BASE_SRC_TOM_CRYPT}/modes/lrw/lrw_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/lrw/lrw_getiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/lrw/lrw_process.c
        ${BASE_SRC_TOM_CRYPT}/modes/lrw/lrw_setiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/lrw/lrw_start.c
        ${BASE_SRC_TOM_CRYPT}/modes/lrw/lrw_test.c

        ${BASE_SRC_TOM_CRYPT}/modes/ofb/ofb_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/ofb/ofb_done.c
        ${BASE_SRC_TOM_CRYPT}/modes/ofb/ofb_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/ofb/ofb_getiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/ofb/ofb_setiv.c
        ${BASE_SRC_TOM_CRYPT}/modes/ofb/ofb_start.c

        ${BASE_SRC_TOM_CRYPT}/modes/xts/xts_decrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/xts/xts_done.c
        ${BASE_SRC_TOM_CRYPT}/modes/xts/xts_encrypt.c
        ${BASE_SRC_TOM_CRYPT}/modes/xts/xts_init.c
        ${BASE_SRC_TOM_CRYPT}/modes/xts/xts_mult_x.c
        ${BASE_SRC_TOM_CRYPT}/modes/xts/xts_test.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/bit/der_decode_bit_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/bit/der_decode_raw_bit_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/bit/der_encode_bit_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/bit/der_encode_raw_bit_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/bit/der_length_bit_string.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/boolean/der_decode_boolean.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/boolean/der_encode_boolean.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/boolean/der_length_boolean.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/choice/der_decode_choice.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/generalizedtime/der_decode_generalizedtime.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/generalizedtime/der_encode_generalizedtime.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/generalizedtime/der_length_generalizedtime.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/ia5/der_decode_ia5_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/ia5/der_encode_ia5_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/ia5/der_length_ia5_string.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/integer/der_decode_integer.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/integer/der_encode_integer.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/integer/der_length_integer.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/object_identifier/der_decode_object_identifier.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/object_identifier/der_encode_object_identifier.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/object_identifier/der_length_object_identifier.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/octet/der_decode_octet_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/octet/der_encode_octet_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/octet/der_length_octet_string.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/printable_string/der_decode_printable_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/printable_string/der_encode_printable_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/printable_string/der_length_printable_string.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_decode_sequence_ex.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_decode_sequence_flexi.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_decode_sequence_multi.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_decode_subject_public_key_info.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_encode_sequence_ex.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_encode_sequence_multi.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_encode_subject_public_key_info.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_length_sequence.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_sequence_free.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/sequence/der_sequence_shrink.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/set/der_encode_set.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/set/der_encode_setof.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/short_integer/der_decode_short_integer.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/short_integer/der_encode_short_integer.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/short_integer/der_length_short_integer.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/teletex_string/der_decode_teletex_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/teletex_string/der_length_teletex_string.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/utctime/der_decode_utctime.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/utctime/der_encode_utctime.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/utctime/der_length_utctime.c

        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/utf8/der_decode_utf8_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/utf8/der_encode_utf8_string.c
        ${BASE_SRC_TOM_CRYPT}/pk/asn1/der/utf8/der_length_utf8_string.c

        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_decrypt_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_encrypt_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_export.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_free.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_generate_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_generate_pqg.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_import.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_make_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_set.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_shared_secret.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_sign_hash.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_verify_hash.c
        ${BASE_SRC_TOM_CRYPT}/pk/dsa/dsa_verify_key.c

        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_ansi_x963_export.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_ansi_x963_import.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_decrypt_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_encrypt_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_export.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_free.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_get_size.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_import.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_make_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_shared_secret.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_sign_hash.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_sizes.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_test.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ecc_verify_hash.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ltc_ecc_is_valid_idx.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ltc_ecc_map.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ltc_ecc_mul2add.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ltc_ecc_mulmod.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ltc_ecc_mulmod_timing.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ltc_ecc_points.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ltc_ecc_projective_add_point.c
        ${BASE_SRC_TOM_CRYPT}/pk/ecc/ltc_ecc_projective_dbl_point.c

        ${BASE_SRC_TOM_CRYPT}/pk/katja/katja_decrypt_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/katja/katja_encrypt_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/katja/katja_export.c
        ${BASE_SRC_TOM_CRYPT}/pk/katja/katja_exptmod.c
        ${BASE_SRC_TOM_CRYPT}/pk/katja/katja_free.c
        ${BASE_SRC_TOM_CRYPT}/pk/katja/katja_import.c
        ${BASE_SRC_TOM_CRYPT}/pk/katja/katja_make_key.c

        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_i2osp.c
        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_mgf1.c
        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_oaep_decode.c
        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_oaep_encode.c
        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_os2ip.c
        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_pss_decode.c
        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_pss_encode.c
        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_v1_5_decode.c
        ${BASE_SRC_TOM_CRYPT}/pk/pkcs1/pkcs_1_v1_5_encode.c

        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_decrypt_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_encrypt_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_export.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_exptmod.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_free.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_get_size.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_import.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_import_pkcs8.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_import_x509.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_make_key.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_set.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_sign_hash.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_sign_saltlen_get.c
        ${BASE_SRC_TOM_CRYPT}/pk/rsa/rsa_verify_hash.c

        ${BASE_SRC_TOM_CRYPT}/prngs/chacha20.c
        ${BASE_SRC_TOM_CRYPT}/prngs/fortuna.c
        ${BASE_SRC_TOM_CRYPT}/prngs/rc4.c
        ${BASE_SRC_TOM_CRYPT}/prngs/rng_get_bytes.c
        ${BASE_SRC_TOM_CRYPT}/prngs/rng_make_prng.c
        ${BASE_SRC_TOM_CRYPT}/prngs/sober128.c
        # ${BASE_SRC_TOM_CRYPT}/prngs/sober128tab.c
        ${BASE_SRC_TOM_CRYPT}/prngs/sprng.c
        ${BASE_SRC_TOM_CRYPT}/prngs/yarrow.c

        ${BASE_SRC_TOM_CRYPT}/stream/chacha/chacha_crypt.c
        ${BASE_SRC_TOM_CRYPT}/stream/chacha/chacha_done.c
        ${BASE_SRC_TOM_CRYPT}/stream/chacha/chacha_ivctr32.c
        ${BASE_SRC_TOM_CRYPT}/stream/chacha/chacha_ivctr64.c
        ${BASE_SRC_TOM_CRYPT}/stream/chacha/chacha_keystream.c
        ${BASE_SRC_TOM_CRYPT}/stream/chacha/chacha_setup.c
        ${BASE_SRC_TOM_CRYPT}/stream/chacha/chacha_test.c

        ${BASE_SRC_TOM_CRYPT}/stream/rc4/rc4_stream.c
        ${BASE_SRC_TOM_CRYPT}/stream/rc4/rc4_test.c

        ${BASE_SRC_TOM_CRYPT}/stream/sober128/sober128_stream.c
        ${BASE_SRC_TOM_CRYPT}/stream/sober128/sober128_test.c
        ${BASE_SRC_TOM_CRYPT}/stream/sober128/sober128tab.c
        )

set(SRC_DROPBEAR
        ${BASE_SRC_DROPBEAR}/atomicio.c
        ${BASE_SRC_DROPBEAR}/bignum.c
        ${BASE_SRC_DROPBEAR}/buffer.c
        ${BASE_SRC_DROPBEAR}/chachapoly.c
        ${BASE_SRC_DROPBEAR}/circbuffer.c
        ${BASE_SRC_DROPBEAR}/common-algo.c
        ${BASE_SRC_DROPBEAR}/common-channel.c
        ${BASE_SRC_DROPBEAR}/common-chansession.c
        ${BASE_SRC_DROPBEAR}/common-kex.c
        ${BASE_SRC_DROPBEAR}/common-runopts.c
        ${BASE_SRC_DROPBEAR}/common-session.c
        ${BASE_SRC_DROPBEAR}/compat.c
        ${BASE_SRC_DROPBEAR}/crypto_desc.c
        ${BASE_SRC_DROPBEAR}/curve25519.c
        ${BASE_SRC_DROPBEAR}/dbhelpers.c
        ${BASE_SRC_DROPBEAR}/dbmalloc.c
        ${BASE_SRC_DROPBEAR}/dbrandom.c
        ${BASE_SRC_DROPBEAR}/dbutil.c
        ${BASE_SRC_DROPBEAR}/dh_groups.c
        ${BASE_SRC_DROPBEAR}/dss.c
        ${BASE_SRC_DROPBEAR}/ecc.c
        ${BASE_SRC_DROPBEAR}/ecdsa.c
        ${BASE_SRC_DROPBEAR}/ed25519.c
        ${BASE_SRC_DROPBEAR}/gcm.c
        ${BASE_SRC_DROPBEAR}/gendss.c
        ${BASE_SRC_DROPBEAR}/gened25519.c
        ${BASE_SRC_DROPBEAR}/genrsa.c
        ${BASE_SRC_DROPBEAR}/gensignkey.c
        ${BASE_SRC_DROPBEAR}/keyimport.c
        ${BASE_SRC_DROPBEAR}/list.c
        ${BASE_SRC_DROPBEAR}/listener.c
        ${BASE_SRC_DROPBEAR}/loginrec.c
        ${BASE_SRC_DROPBEAR}/ltc_prng.c
        ${BASE_SRC_DROPBEAR}/netio.c
        ${BASE_SRC_DROPBEAR}/packet.c
        ${BASE_SRC_DROPBEAR}/process-packet.c
        ${BASE_SRC_DROPBEAR}/queue.c
        ${BASE_SRC_DROPBEAR}/rsa.c
        ${BASE_SRC_DROPBEAR}/signkey.c
        ${BASE_SRC_DROPBEAR}/signkey_ossh.c
        ${BASE_SRC_DROPBEAR}/sk-ecdsa.c
        ${BASE_SRC_DROPBEAR}/sk-ed25519.c
        ${BASE_SRC_DROPBEAR}/sshpty.c
        ${BASE_SRC_DROPBEAR}/svr-auth.c
        ${BASE_SRC_DROPBEAR}/svr-authpasswd.c
        ${BASE_SRC_DROPBEAR}/svr-authpubkey.c
        ${BASE_SRC_DROPBEAR}/svr-authpubkeyoptions.c
        ${BASE_SRC_DROPBEAR}/svr-chansession.c
        ${BASE_SRC_DROPBEAR}/svr-kex.c
        ${BASE_SRC_DROPBEAR}/svr-main.c
        ${BASE_SRC_DROPBEAR}/svr-runopts.c
        ${BASE_SRC_DROPBEAR}/svr-service.c
        ${BASE_SRC_DROPBEAR}/svr-session.c
        ${BASE_SRC_DROPBEAR}/svr-tcpfwd.c
        ${BASE_SRC_DROPBEAR}/tcp-accept.c
        ${BASE_SRC_DROPBEAR}/termcodes.c
        )

set(SRC_DROPBEAR_SCP
        ${BASE_SRC_DROPBEAR}/scp.c
        ${BASE_SRC_DROPBEAR}/scpmisc.c
        ${BASE_SRC_DROPBEAR}/atomicio.c
        )

set(SRC_SFTP_SERVER
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/bsd-closefrom.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/bsd-misc.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/explicit_bzero.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/fmt_scaled.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/freezero.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/getopt_long.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/pwcache.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/reallocarray.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/recallocarray.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/strmode.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/strtonum.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/timingsafe_bcmp.c
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat/vis.c
        ${BASE_SRC_SFTP_SERVER}/addr.c
        ${BASE_SRC_SFTP_SERVER}/addrmatch.c
        ${BASE_SRC_SFTP_SERVER}/atomicio.c
        ${BASE_SRC_SFTP_SERVER}/entropy.c
        ${BASE_SRC_SFTP_SERVER}/fatal.c
        ${BASE_SRC_SFTP_SERVER}/log.c
        ${BASE_SRC_SFTP_SERVER}/platform-misc.c
        ${BASE_SRC_SFTP_SERVER}/platform-pledge.c
        ${BASE_SRC_SFTP_SERVER}/platform-tracing.c
        ${BASE_SRC_SFTP_SERVER}/match.c
        ${BASE_SRC_SFTP_SERVER}/misc.c
        ${BASE_SRC_SFTP_SERVER}/sftp-common.c
        ${BASE_SRC_SFTP_SERVER}/sftp-realpath.c
        ${BASE_SRC_SFTP_SERVER}/sftp-server.c
        ${BASE_SRC_SFTP_SERVER}/sftp-server-main.c
        ${BASE_SRC_SFTP_SERVER}/sshbuf.c
        ${BASE_SRC_SFTP_SERVER}/sshbuf-misc.c
        ${BASE_SRC_SFTP_SERVER}/sshbuf-getput-basic.c
        ${BASE_SRC_SFTP_SERVER}/ssherr.c
        ${BASE_SRC_SFTP_SERVER}/xmalloc.c
        )

set(SRC_RSYNC
        ${BASE_SRC_RSYNC}/lib/compat.c
        ${BASE_SRC_RSYNC}/lib/getpass.c
        ${BASE_SRC_RSYNC}/lib/md5.c
        ${BASE_SRC_RSYNC}/lib/mdfour.c
        ${BASE_SRC_RSYNC}/lib/permstring.c
        ${BASE_SRC_RSYNC}/lib/pool_alloc.c
        ${BASE_SRC_RSYNC}/lib/snprintf.c
        ${BASE_SRC_RSYNC}/lib/sysacls.c
        ${BASE_SRC_RSYNC}/lib/sysxattrs.c
        ${BASE_SRC_RSYNC}/lib/wildmatch.c

        ${BASE_SRC_RSYNC}/popt/findme.c
        ${BASE_SRC_RSYNC}/popt/popt.c
        ${BASE_SRC_RSYNC}/popt/poptconfig.c
        ${BASE_SRC_RSYNC}/popt/popthelp.c
        ${BASE_SRC_RSYNC}/popt/poptparse.c

        ${BASE_SRC_RSYNC}/zlib/adler32.c
        ${BASE_SRC_RSYNC}/zlib/compress.c
        ${BASE_SRC_RSYNC}/zlib/crc32.c
        ${BASE_SRC_RSYNC}/zlib/deflate.c
        ${BASE_SRC_RSYNC}/zlib/inffast.c
        ${BASE_SRC_RSYNC}/zlib/inflate.c
        ${BASE_SRC_RSYNC}/zlib/inftrees.c
        ${BASE_SRC_RSYNC}/zlib/trees.c
        ${BASE_SRC_RSYNC}/zlib/zutil.c

        ${BASE_SRC_RSYNC}/access.c
        ${BASE_SRC_RSYNC}/acls.c
        ${BASE_SRC_RSYNC}/authenticate.c
        ${BASE_SRC_RSYNC}/backup.c
        ${BASE_SRC_RSYNC}/batch.c
        ${BASE_SRC_RSYNC}/checksum.c
        ${BASE_SRC_RSYNC}/chmod.c
        ${BASE_SRC_RSYNC}/cleanup.c
        ${BASE_SRC_RSYNC}/clientname.c
        ${BASE_SRC_RSYNC}/clientserver.c
        ${BASE_SRC_RSYNC}/compat.c
        ${BASE_SRC_RSYNC}/connection.c
        ${BASE_SRC_RSYNC}/delete.c
        ${BASE_SRC_RSYNC}/exclude.c
        ${BASE_SRC_RSYNC}/fileio.c
        ${BASE_SRC_RSYNC}/flist.c
        ${BASE_SRC_RSYNC}/generator.c
        ${BASE_SRC_RSYNC}/hashtable.c
        ${BASE_SRC_RSYNC}/hlink.c
        ${BASE_SRC_RSYNC}/io.c
        ${BASE_SRC_RSYNC}/loadparm.c
        ${BASE_SRC_RSYNC}/log.c
        ${BASE_SRC_RSYNC}/main.c
        ${BASE_SRC_RSYNC}/match.c
        ${BASE_SRC_RSYNC}/options.c
        ${BASE_SRC_RSYNC}/params.c
        ${BASE_SRC_RSYNC}/pipe.c
        ${BASE_SRC_RSYNC}/progress.c
        ${BASE_SRC_RSYNC}/receiver.c
        ${BASE_SRC_RSYNC}/rsync.c
        ${BASE_SRC_RSYNC}/sender.c
        ${BASE_SRC_RSYNC}/socket.c
        ${BASE_SRC_RSYNC}/syscall.c
        ${BASE_SRC_RSYNC}/token.c
        ${BASE_SRC_RSYNC}/uidlist.c
        ${BASE_SRC_RSYNC}/usage.c
        ${BASE_SRC_RSYNC}/util1.c
        ${BASE_SRC_RSYNC}/util2.c
        ${BASE_SRC_RSYNC}/xattrs.c
        )
# #############################################################################

# Code modifications are flagged with
#   SSHD4A_REQUIRED_CHANGE
# Used in comments to indicate code changes which are unconditional.
# and the below
#   SSHD4A_EXTEND_AUTHENTICATION
#   SSHD4A_ALLOW_RSA_KEY_SIGNATURE_MISMATCH
add_compile_definitions(
        # Indicate that "options.h" MUST read the "localoptions.h"
        LOCALOPTIONS_H_EXISTS
        # Enable the code to support:
        # - public key logins
        # - single-use passwords
        # - locally stored user/password
        SSHD4A_EXTEND_AUTHENTICATION
        # Enable the code that allows mismatches between key and signatures in some bad clients.
        # TODO: review if we really want this?
        SSHD4A_ALLOW_RSA_KEY_SIGNATURE_MISMATCH
)

# compile tommath as an object library; to be statically linked later.
add_library(tommath OBJECT ${SRC_TOM_MATH})
target_include_directories(tommath PRIVATE ${DROPBEAR_INCLUDES})

# compile tomcrypt as an object library; to be statically linked later.
add_library(tomcrypt OBJECT ${SRC_TOM_CRYPT})
# LTC_SOURCE is ONLY valid/used for tomcrypt.
target_compile_definitions(tomcrypt PRIVATE LTC_SOURCE)
target_include_directories(tomcrypt PRIVATE ${DROPBEAR_INCLUDES})

# compile dropbear as an object library; to be statically linked later.
add_library(dropbear OBJECT ${SRC_DROPBEAR})
target_include_directories(dropbear PRIVATE ${DROPBEAR_INCLUDES})

# finally, build the jni code with embedded dropbear by
# linking it with the above object libraries + zlib from the system.
add_library(jni-dropbear SHARED src/main/cpp/jni-dropbear.c)
target_link_options(jni-dropbear PRIVATE -lz)
target_link_libraries(jni-dropbear tommath tomcrypt dropbear)
target_include_directories(jni-dropbear PRIVATE ${DROPBEAR_INCLUDES})

# #############################################################################
# build scp executable
add_executable(libscp.so ${SRC_DROPBEAR_SCP})
target_include_directories(libscp.so PRIVATE ${DROPBEAR_INCLUDES})

# #############################################################################
# build sftp executable
add_executable(libsftp-server.so ${SRC_SFTP_SERVER})
target_include_directories(libsftp-server.so PRIVATE
        ${BASE_SRC_SFTP_SERVER}
        ${BASE_SRC_SFTP_SERVER}/openbsd-compat
        )

# #############################################################################
# build rsync executable
# use internal zlib; see app/src/main/cpp/rsync/zlib/README.rsync
add_executable(librsync.so ${SRC_RSYNC})
# needed for popt
target_compile_definitions(librsync.so PRIVATE HAVE_CONFIG_H)
target_include_directories(librsync.so PRIVATE
        ${BASE_SRC_RSYNC}
        ${BASE_SRC_RSYNC}/popt
        ${BASE_SRC_RSYNC}/zlib
        )
