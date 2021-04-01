package com.mapo.walkaholic.data.model

data class KakaoAccount(
    val age_range_needs_agreement: Boolean,
    val birthday_needs_agreement: Boolean,
    val email: String,
    val email_needs_agreement: Boolean,
    val gender_needs_agreement: Boolean,
    val has_age_range: Boolean,
    val has_birthday: Boolean,
    val has_email: Boolean,
    val has_gender: Boolean,
    val is_email_valid: Boolean,
    val is_email_verified: Boolean,
    val profile: Profile,
    val profile_needs_agreement: Boolean
)