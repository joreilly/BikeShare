//
//  KMMVMViewModelScope.h
//  KMMViewModelCoreObjC
//
//  Created by Rick Clephas on 27/11/2022.
//

#ifndef KMMVMViewModelScope_h
#define KMMVMViewModelScope_h

#import <Foundation/Foundation.h>

__attribute__((swift_name("ViewModelScope")))
@protocol KMMVMViewModelScope
- (void)increaseSubscriptionCount;
- (void)decreaseSubscriptionCount;
- (void)setSendObjectWillChange:(void (^ _Nonnull)(void))sendObjectWillChange;
- (void)cancel;
@end

#endif /* KMMVMViewModelScope_h */
