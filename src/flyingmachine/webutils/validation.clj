(ns ^{:doc "validation: combination of field name and validation checks

validation check group: a seq of alternating error messages and
validation functions. If a validation function returns false then the
error message is added to a list of error messages for the given
field.

validation check: a function to apply to the value corresponding to
the field name specified in the validation"}

  flyingmachine.webutils.validation)


(defn error-messages-for
  "return a vector of error messages or nil if no errors
   validation-check-groups is a seq of alternating messages and
   validation checks"
  [value validation-check-groups]
  (for [[error-message validation-check] (partition 2 validation-check-groups)
        :when (not (validation-check value))]
    error-message))

(defn validate
  "returns a map with a vec of errors for each key"
  [to-validate validations]
  (loop [errors {}
         remaining-validations (seq validations)]
    (if-let [validation (first remaining-validations)]
      (let [[fieldname validation-check-groups] validation
            value (get to-validate fieldname)
            error-messages (error-messages-for value validation-check-groups)]
        (if (empty? error-messages)
          (recur errors (rest remaining-validations))
          (recur (assoc errors fieldname error-messages) (rest remaining-validations))))
      errors)))

(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))