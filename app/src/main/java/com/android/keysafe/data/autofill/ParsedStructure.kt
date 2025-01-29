package com.android.keysafe.data.autofill

import android.view.autofill.AutofillId

data class ParsedStructure(
    var userNameId: AutofillId,
    var passwordId: AutofillId,
    var saveType: Int
)