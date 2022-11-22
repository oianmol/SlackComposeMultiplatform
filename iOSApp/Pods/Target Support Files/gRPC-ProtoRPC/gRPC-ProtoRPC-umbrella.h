#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "ProtoRPCLegacy.h"
#import "ProtoMethod.h"
#import "ProtoRPC.h"
#import "ProtoService.h"

FOUNDATION_EXPORT double ProtoRPCVersionNumber;
FOUNDATION_EXPORT const unsigned char ProtoRPCVersionString[];

