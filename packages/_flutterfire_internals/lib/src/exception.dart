// ignore_for_file: require_trailing_commas
// Copyright 2020, the Chromium project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/services.dart';
import 'package:stack_trace/stack_trace.dart';

/// Catches a [PlatformException] and returns an [Exception].
///
/// If the [Exception] is a [PlatformException], a [FirebaseException] is returned.
Never convertPlatformExceptionToFirebaseException(
  Object exception,
  StackTrace rawStackTrace, {
  required String plugin,
}) {
  var stackTrace = rawStackTrace;
  if (stackTrace == StackTrace.empty) {
    stackTrace = StackTrace.current;
  }

  if (exception is! PlatformException) {
    Error.throwWithStackTrace(exception, stackTrace);
  }

  Error.throwWithStackTrace(
    platformExceptionToFirebaseException(exception, plugin: plugin),
    stackTrace,
  );
}

/// Converts a [PlatformException] into a [FirebaseException].
///
/// A [PlatformException] can only be converted to a [FirebaseException] if the
/// `details` of the exception exist. Firebase returns specific codes and messages
/// which can be converted into user friendly exceptions.
FirebaseException platformExceptionToFirebaseException(
  PlatformException platformException, {
  required String plugin,
}) {
  Map<String, Object>? details;

  final rawDetails = platformException.details;

  if (rawDetails is Map) {
    details = Map<String, Object>.from(rawDetails);
  }

  String? code;
  String message = platformException.message ?? '';

  code = details?['code'] as String?;
  message = details?['message'] as String? ?? message;

  return FirebaseException(
    plugin: plugin,
    code: code ?? 'unknown',
    message: message,
  );
}

/// A custom [EventChannel] with default error handling logic.
extension EventChannelExtension on EventChannel {
  /// Similar to [receiveBroadcastStream], but with enforced error handling.
  Stream<dynamic> receiveGuardedBroadcastStream({
    dynamic arguments,
    required dynamic Function(Object error, StackTrace stackTrace) onError,
  }) {
    final incomingStackTrace = StackTrace.current;

    return receiveBroadcastStream(arguments).handleError((
      Object error,
      StackTrace stackTrace,
    ) {
      if (stackTrace == StackTrace.empty) {
        return onError(error, incomingStackTrace);
      }

      return onError(
        error,
        Chain([Trace.from(stackTrace), Trace.from(incomingStackTrace)]),
      );
    });
  }
}
